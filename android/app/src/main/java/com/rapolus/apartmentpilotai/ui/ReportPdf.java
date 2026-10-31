package com.rapolus.apartmentpilotai.ui;

import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import com.rapolus.apartmentpilotai.data.ReportText;
import com.rapolus.apartmentpilotai.data.StatusTone;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Standard A4 report layout shared by every generated PDF: a pine header band, summary cards, tables with
 * coloured headers and banded rows, simple bar charts and a footer with the brand line and "Page X of Y".
 * Callers describe content with blocks; this class only draws, it never computes report figures.
 */
public final class ReportPdf {
    // A4 portrait in PDF points.
    private static final float W=595,H=842,M=36,CONTENT=W-2*M,BOTTOM=H-48;
    private static final int PINE=0xff174c40,PINE2=0xff24594a,INK=0xff1b2c28,MUTED=0xff606c65,RULE=0xffe1e6dd,BAND=0xfff6f7f3,
        MINT=0xffe9f4ed,GREEN=0xff276e4a,AMBER=0xff906012,AMBG=0xfffff1d6,RED=0xffb24840,REDBG=0xfffdece6,BLUE=0xff466da8,BLUEBG=0xffeaf0fc,
        VIOLET=0xff7662a7,VBG=0xfff0ecfb,HEAD_SUB=0xffc3d7c6,HEAD_META=0xffd3e0d4,WHITE=0xffffffff;

    /** One report: header facts plus the blocks drawn in order. */
    public static final class Doc {
        final String apartment,title,period,reference;
        final List<Object> blocks=new ArrayList<>();
        public Doc(String apartment,String title,String period,String reference) {
            this.apartment=apartment==null||apartment.trim().isEmpty()?"ApartmentCare":apartment.trim();
            this.title=title;
            this.period=period==null?"":period;
            this.reference=reference==null?"":reference;
        }
        /** Name used for the shared file. */
        public String fileTitle() {
            return title+(period.isEmpty()?"":" · "+period);
        }
        public Doc cards(String[][] labelValue,int highlight) {
            blocks.add(new Cards(labelValue,highlight));
            return this;
        }
        public Doc section(String title) {
            blocks.add(new Section(title));
            return this;
        }
        public Doc table(Table t) {
            blocks.add(t);
            return this;
        }
        public Doc bars(String[] labels,BigDecimal[] values,int[] tones) {
            blocks.add(new Bars(labels,values,tones));
            return this;
        }
        public Doc details(List<String[]> rows) {
            blocks.add(new Details(rows));
            return this;
        }
        public Doc amount(String label,BigDecimal value,String stamp,boolean problem) {
            blocks.add(new Amount(label,value,stamp,problem));
            return this;
        }
        public Doc note(String text) {
            blocks.add(new Note(text));
            return this;
        }
    }
    /** Tones for bars. */
    public static final int TONE_GOOD=0,TONE_WAIT=1,TONE_BAD=2;

    public static final class Table {
        final String[] headers;
        final float[] weights;
        final boolean[] numeric;
        final List<String[]> rows=new ArrayList<>();
        String[] total;
        int statusColumn=-1;
        public Table(String[] headers,float[] weights,boolean[] numeric) {
            this.headers=headers;
            this.weights=weights;
            this.numeric=numeric;
        }
        public Table row(String... cells) {
            rows.add(cells);
            return this;
        }
        public Table total(String... cells) {
            total=cells;
            return this;
        }
        /** Column whose text is drawn as a coloured status chip. */
        public Table status(int column) {
            statusColumn=column;
            return this;
        }
    }
    private static final class Cards {
        final String[][] items;
        final int highlight;
        Cards(String[][] items,int highlight) {
            this.items=items;
            this.highlight=highlight;
        }
    }
    private static final class Section {
        final String title;
        Section(String title) {
            this.title=title;
        }
    }
    private static final class Bars {
        final String[] labels;
        final BigDecimal[] values;
        final int[] tones;
        Bars(String[] labels,BigDecimal[] values,int[] tones) {
            this.labels=labels;
            this.values=values;
            this.tones=tones;
        }
    }
    private static final class Details {
        final List<String[]> rows;
        Details(List<String[]> rows) {
            this.rows=rows;
        }
    }
    private static final class Amount {
        final String label,stamp;
        final BigDecimal value;
        final boolean problem;
        Amount(String label,BigDecimal value,String stamp,boolean problem) {
            this.label=label;
            this.value=value;
            this.stamp=stamp;
            this.problem=problem;
        }
    }
    private static final class Note {
        final String text;
        Note(String text) {
            this.text=text;
        }
    }

    /** Writes the report as a PDF file. Laid out twice: once to count pages, once to draw "Page X of Y". */
    public static void write(Doc doc,String generated,File target)throws IOException {
        int pages=new Painter(doc,generated,null,0).run();
        PdfDocument pdf=new PdfDocument();
        try {
            new Painter(doc,generated,pdf,pages).run();
            try(OutputStream out=new FileOutputStream(target)) {
                pdf.writeTo(out);
            }
        } finally {
            pdf.close();
        }
    }

    private static final class Painter {
        final Doc doc;
        final String generated;
        final PdfDocument pdf;
        final int total;
        final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        final Typeface regular=Typeface.create("sans-serif",Typeface.NORMAL),bold=Typeface.create("sans-serif",Typeface.BOLD),
            medium=Typeface.create("sans-serif-medium",Typeface.NORMAL),serif=Typeface.create(Typeface.SERIF,Typeface.BOLD);
        PdfDocument.Page page;
        Canvas c;
        int number=0;
        float y;
        Painter(Doc doc,String generated,PdfDocument pdf,int total) {
            this.doc=doc;
            this.generated=generated;
            this.pdf=pdf;
            this.total=total;
        }
        int run() {
            newPage();
            for(Object block:doc.blocks) {
                if(block instanceof Cards)cards((Cards)block);
                else if(block instanceof Section)section((Section)block);
                else if(block instanceof Table)table((Table)block);
                else if(block instanceof Bars)bars((Bars)block);
                else if(block instanceof Details)details((Details)block);
                else if(block instanceof Amount)amount((Amount)block);
                else if(block instanceof Note)note((Note)block);
                y+=12;
            }
            finishPage();
            return number;
        }
        // ---------- page frame ----------
        void newPage() {
            if(page!=null||c!=null)finishPage();
            number++;
            if(pdf!=null) {
                page=pdf.startPage(new PdfDocument.PageInfo.Builder((int)W,(int)H,number).create());
                c=page.getCanvas();
            }
            y=header(number==1);
        }
        void finishPage() {
            footer();
            if(pdf!=null&&page!=null)pdf.finishPage(page);
            page=null;
            c=null;
        }
        float header(boolean first) {
            float h=first?78:44;
            if(c!=null) {
                Paint band=new Paint(Paint.ANTI_ALIAS_FLAG);
                band.setShader(new LinearGradient(0,0,W,0,PINE,PINE2,Shader.TileMode.CLAMP));
                c.drawRect(0,0,W,h,band);
            }
            float right=W-M;
            if(first) {
                text(doc.apartment.toUpperCase(Locale.ROOT),M,26,8.5f,bold,HEAD_SUB,false,CONTENT*0.62f);
                text(doc.title,M,50,19,serif,WHITE,false,CONTENT*0.62f);
                String meta=doc.reference.isEmpty()?doc.period:doc.reference;
                text(meta,right,26,10,bold,WHITE,true,CONTENT*0.36f);
                if(!doc.reference.isEmpty()&&!doc.period.isEmpty())text(doc.period,right,40,8.5f,regular,HEAD_META,true,CONTENT*0.36f);
                text("Generated "+generated,right,doc.reference.isEmpty()||doc.period.isEmpty()?40:53,8,regular,HEAD_META,true,CONTENT*0.36f);
            } else {
                text(doc.title+(doc.period.isEmpty()?"":" · "+doc.period),M,27,11,bold,WHITE,false,CONTENT*0.7f);
                text(doc.apartment,right,27,8.5f,regular,HEAD_SUB,true,CONTENT*0.28f);
            }
            return h+18;
        }
        void footer() {
            if(c==null)return;
            float top=H-34;
            fill(M,top,W-M,top+0.8f,RULE);
            text("Powered By @Rapolu`s",M,top+14,8,medium,MUTED,false,CONTENT/3);
            centered("Generated by ApartmentCare",W/2,top+14,8,regular,MUTED);
            text("Page "+number+" of "+Math.max(total,number),W-M,top+14,8,medium,MUTED,true,CONTENT/3);
        }
        /** Starts a new page unless {@code height} more points fit above the footer. */
        void need(float height) {
            if(y+height>BOTTOM)newPage();
        }
        // ---------- blocks ----------
        void cards(Cards b) {
            int n=b.items.length;
            if(n==0)return;
            int perRow=Math.min(4,n);
            float gap=8,w=(CONTENT-gap*(perRow-1))/perRow,h=48;
            for(int i=0;i<n;i++) {
                int col=i%perRow;
                if(col==0) {
                    if(i>0)y+=h+gap;
                    need(h);
                }
                float x=M+col*(w+gap);
                boolean hl=i==b.highlight;
                round(x,y,x+w,y+h,6,hl?MINT:WHITE,hl?0xffcfe5d6:RULE);
                text(b.items[i][0].toUpperCase(Locale.ROOT),x+9,y+15,7,bold,MUTED,false,w-18);
                float size=13.5f;
                p.setTypeface(bold);
                p.setTextSize(size);
                while(size>8&&p.measureText(b.items[i][1])>w-18) {
                    size-=0.5f;
                    p.setTextSize(size);
                }
                text(b.items[i][1],x+9,y+36,size,bold,INK,false,w-18);
            }
            y+=h;
        }
        void section(Section b) {
            need(48); // keep a heading with at least the first line under it
            text(b.title.toUpperCase(Locale.ROOT),M,y+8,8.5f,bold,PINE,false,CONTENT);
            fill(M,y+13,W-M,y+14.5f,PINE);
            y+=24;
        }
        void table(Table t) {
            float sum=0;
            for(float w:t.weights)sum+=w;
            float[] widths=new float[t.weights.length];
            for(int i=0;i<widths.length;i++)widths[i]=CONTENT*t.weights[i]/sum;
            float pad=6,line=11.5f;
            tableHeader(t,widths,pad);
            for(int r=0;r<t.rows.size();r++)tableRow(t,t.rows.get(r),widths,pad,line,r%2==1?BAND:WHITE,false);
            if(t.total!=null)tableRow(t,t.total,widths,pad,line,MINT,true);
        }
        void tableHeader(Table t,float[] widths,float pad) {
            need(20+24);
            fill(M,y,W-M,y+20,PINE);
            float x=M;
            for(int i=0;i<t.headers.length;i++) {
                float right=x+widths[i]-pad;
                if(t.numeric[i])text(t.headers[i],right,y+13.5f,8,bold,WHITE,true,widths[i]-2*pad);
                else text(t.headers[i],x+pad,y+13.5f,8,bold,WHITE,false,widths[i]-2*pad);
                x+=widths[i];
            }
            y+=20;
        }
        void tableRow(Table t,String[] cells,float[] widths,float pad,float line,int background,boolean isTotal) {
            Typeface face=isTotal?bold:regular;
            p.setTypeface(face);
            p.setTextSize(8.5f);
            List<List<String>> wrapped=new ArrayList<>();
            int lines=1;
            for(int i=0;i<widths.length;i++) {
                String cell=i<cells.length&&cells[i]!=null?cells[i]:"";
                List<String> l=i==t.statusColumn&&!isTotal?Collections.singletonList(cell):ReportText.wrap(cell,widths[i]-2*pad,p::measureText);
                wrapped.add(l);
                lines=Math.max(lines,l.size());
            }
            float h=lines*line+9;
            if(y+h>BOTTOM) {
                newPage();
                tableHeader(t,widths,pad);
            }
            fill(M,y,W-M,y+h,background);
            if(isTotal)fill(M,y,W-M,y+1.3f,PINE);
            fill(M,y+h-0.6f,W-M,y+h,RULE);
            float x=M;
            for(int i=0;i<widths.length;i++) {
                List<String> l=wrapped.get(i);
                if(i==t.statusColumn&&!isTotal&&!l.get(0).isEmpty())chip(l.get(0),x+pad,y+4.5f);
                else for(int k=0;k<l.size();k++) {
                    float baseline=y+12+k*line;
                    if(t.numeric[i])text(l.get(k),x+widths[i]-pad,baseline,8.5f,face,INK,true,widths[i]-2*pad);
                    else text(l.get(k),x+pad,baseline,8.5f,face,INK,false,widths[i]-2*pad);
                }
                x+=widths[i];
            }
            y+=h;
        }
        void chip(String status,float x,float top) {
            int fg,bg;
            switch(StatusTone.of(status)) {
                case DONE: fg=GREEN; bg=MINT; break;
                case WAITING: fg=AMBER; bg=AMBG; break;
                case REVIEW: fg=BLUE; bg=BLUEBG; break;
                case UPCOMING: fg=VIOLET; bg=VBG; break;
                case PROBLEM: fg=RED; bg=REDBG; break;
                default: fg=MUTED; bg=BAND;
            }
            p.setTypeface(bold);
            p.setTextSize(7.5f);
            float w=p.measureText(status)+12;
            round(x,top,x+w,top+12,6,bg,0);
            text(status,x+6,top+8.8f,7.5f,bold,fg,false,w);
        }
        void bars(Bars b) {
            BigDecimal max=BigDecimal.ZERO;
            for(BigDecimal v:b.values)if(v.abs().compareTo(max)>0)max=v.abs();
            float labelW=120,valueW=96,barX=M+labelW,barW=CONTENT-labelW-valueW-8,row=17;
            for(int i=0;i<b.labels.length;i++) {
                need(row);
                text(b.labels[i],M,y+9,8.5f,regular,INK,false,labelW-8);
                round(barX,y+2,barX+barW,y+10,4,BAND,0);
                float share=max.signum()==0?0:b.values[i].abs().floatValue()/max.floatValue();
                int tone=b.tones==null?TONE_GOOD:b.tones[i];
                if(share>0)round(barX,y+2,barX+Math.max(8,barW*share),y+10,4,tone==TONE_BAD?RED:tone==TONE_WAIT?AMBER:PINE2,0);
                text(ReportText.money(b.values[i]),W-M,y+9,8.5f,bold,INK,true,valueW);
                y+=row;
            }
        }
        void details(Details b) {
            p.setTextSize(9);
            float keyW=CONTENT*0.42f,valueW=CONTENT-keyW;
            for(String[] r:b.rows) {
                p.setTypeface(bold);
                List<String> value=ReportText.wrap(r[1],valueW-4,p::measureText);
                float h=value.size()*12+10;
                need(h);
                fill(M,y+h-0.6f,W-M,y+h,RULE);
                text(r[0],M,y+13,9,regular,MUTED,false,keyW-6);
                for(int k=0;k<value.size();k++)text(value.get(k),W-M,y+13+k*12,9,bold,INK,true,valueW);
                y+=h;
            }
        }
        void amount(Amount b) {
            need(78);
            text(b.label.toUpperCase(Locale.ROOT),M,y+10,7.5f,bold,MUTED,false,CONTENT*0.6f);
            text(ReportText.money(b.value),M,y+40,28,bold,INK,false,CONTENT*0.66f);
            text(ReportText.words(b.value),M,y+56,8.5f,regular,MUTED,false,CONTENT*0.66f);
            if(b.stamp!=null&&!b.stamp.isEmpty()&&c!=null) {
                int color=b.problem?RED:GREEN;
                p.setTypeface(bold);
                p.setTextSize(15);
                p.setLetterSpacing(0.18f);
                float w=p.measureText(b.stamp)+22,cx=W-M-w/2-6,cy=y+30;
                c.save();
                c.rotate(-6,cx,cy);
                Paint frame=new Paint(Paint.ANTI_ALIAS_FLAG);
                frame.setStyle(Paint.Style.STROKE);
                frame.setStrokeWidth(2);
                frame.setColor(color);
                c.drawRoundRect(new RectF(cx-w/2,cy-14,cx+w/2,cy+14),4,4,frame);
                p.setColor(color);
                p.setTextAlign(Paint.Align.CENTER);
                c.drawText(b.stamp,cx,cy+5.5f,p);
                p.setTextAlign(Paint.Align.LEFT);
                c.restore();
                p.setLetterSpacing(0);
            }
            y+=66;
        }
        void note(Note b) {
            p.setTypeface(regular);
            p.setTextSize(8.5f);
            List<String> lines=ReportText.wrap(b.text,CONTENT-20,p::measureText);
            float h=lines.size()*11.5f+12;
            need(h);
            round(M,y,W-M,y+h,5,BAND,0);
            for(int k=0;k<lines.size();k++)text(lines.get(k),M+10,y+14+k*11.5f,8.5f,regular,MUTED,false,CONTENT-20);
            y+=h;
        }
        // ---------- drawing helpers ----------
        /** Draws one line; text wider than maxWidth is shrunk to fit rather than cut. */
        void text(String s,float x,float baseline,float size,Typeface face,int color,boolean alignRight,float maxWidth) {
            if(c==null||s==null||s.isEmpty())return;
            p.setTypeface(face);
            p.setTextSize(size);
            float w=p.measureText(s);
            if(w>maxWidth&&maxWidth>0) {
                p.setTextSize(Math.max(5,size*maxWidth/w));
                w=p.measureText(s);
            }
            p.setColor(color);
            c.drawText(s,alignRight?x-w:x,baseline,p);
        }
        void centered(String s,float cx,float baseline,float size,Typeface face,int color) {
            if(c==null)return;
            p.setTypeface(face);
            p.setTextSize(size);
            p.setColor(color);
            c.drawText(s,cx-p.measureText(s)/2,baseline,p);
        }
        void fill(float l,float t,float r,float b,int color) {
            if(c==null)return;
            Paint f=new Paint();
            f.setColor(color);
            c.drawRect(l,t,r,b,f);
        }
        void round(float l,float t,float r,float b,float radius,int fillColor,int strokeColor) {
            if(c==null)return;
            Paint f=new Paint(Paint.ANTI_ALIAS_FLAG);
            f.setColor(fillColor);
            RectF rect=new RectF(l,t,r,b);
            c.drawRoundRect(rect,radius,radius,f);
            if(strokeColor!=0) {
                f.setStyle(Paint.Style.STROKE);
                f.setStrokeWidth(0.8f);
                f.setColor(strokeColor);
                c.drawRoundRect(rect,radius,radius,f);
            }
        }
    }
}
