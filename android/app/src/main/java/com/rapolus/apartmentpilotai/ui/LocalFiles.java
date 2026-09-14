package com.rapolus.apartmentpilotai.ui;
import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import androidx.core.content.FileProvider;
import org.json.*;
import java.io.*;
import java.util.*;
/** Actual PDF export and local file opening. Files are short-lived app-private cache copies. */ public final class LocalFiles {
    private final Activity activity;
    public LocalFiles(Activity activity) {
        this.activity=activity;
    }
    private File folder()throws IOException {
        File f=new File(activity.getCacheDir(),"shared");
        if(!f.exists()&&!f.mkdirs())throw new IOException("Could not create export cache.");
        long cutoff=System.currentTimeMillis()-24*60*60*1000L;
        File[] children=f.listFiles();
        if(children!=null)for(File c:children)if(c.isFile()&&c.lastModified()<cutoff)c.delete();
        return f;
    }
    private void launch(File f,String mime,boolean share) {
        Uri uri=FileProvider.getUriForFile(activity,activity.getPackageName()+".files",f);
        Intent i=new Intent(share?Intent.ACTION_SEND:Intent.ACTION_VIEW);
        if(share)i.setType(mime).putExtra(Intent.EXTRA_STREAM,uri);
        else i.setDataAndType(uri,mime);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setClipData(ClipData.newRawUri("Apartment document",uri));
        try {
            activity.startActivity(Intent.createChooser(i,share?"Share document":"Open attachment"));
        } catch(ActivityNotFoundException e) {
            throw new IllegalArgumentException("Install a viewer for this document type.");
        }
    }
    public void open(JSONObject data)throws Exception {
        String mime=data.getString("mime");
        if(!java.util.Arrays.asList("application/pdf","image/png","image/jpeg").contains(mime))throw new IllegalArgumentException("Unsupported document type.");
        byte[] bytes=android.util.Base64.decode(data.getString("content"),android.util.Base64.DEFAULT);
        if(bytes.length>5*1024*1024)throw new IllegalArgumentException("Attachment too large.");
        String ext=mime.equals("application/pdf")?".pdf":mime.equals("image/png")?".png":".jpg";
        File file=new File(folder(),"attachment-"+UUID.randomUUID()+ext);
        try(OutputStream out=new FileOutputStream(file)) {
            out.write(bytes);
        }
        launch(file,mime,false);
    }
    public void exportPdf(String heading,JSONObject data)throws Exception {
        List<String> lines=new ArrayList<>();
        flatten(data,"",lines);
        if(lines.isEmpty())lines.add("No entries");
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(11);
        List<String> wrapped=new ArrayList<>();
        for(String line:lines)for(String paragraph:line.split("\\n",-1)) {
            String remaining=paragraph;
            while(!remaining.isEmpty()) {
                int count=paint.breakText(remaining,true,510,null);
                if(count==0)count=1;
                wrapped.add(remaining.substring(0,count));
                remaining=remaining.substring(count);
            }
            wrapped.add("");
        }
        File target=new File(folder(),"ApartmentPilotAI-"+System.currentTimeMillis()+".pdf");
        PdfDocument pdf=new PdfDocument();
        try {
            int index=0,pageNo=0;
            while(index<wrapped.size()) {
                PdfDocument.Page page=pdf.startPage(new PdfDocument.PageInfo.Builder(595,842,++pageNo).create());
                Canvas canvas=page.getCanvas();
                paint.setColor(Color.BLACK);
                paint.setTextSize(17);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                canvas.drawText(heading.length()>58?heading.substring(0,58):heading,40,48,paint);
                paint.setTypeface(Typeface.DEFAULT);
                paint.setTextSize(9);
                canvas.drawText("Generated "+java.time.LocalDate.now()+" · Page "+pageNo,40,70,paint);
                paint.setTextSize(11);
                int y=101;
                while(index<wrapped.size()&&y<770) {
                    canvas.drawText(wrapped.get(index++),40,y,paint);
                    y+=15;
                }
                paint.setTextSize(9);
                canvas.drawText("Powered By @Rapolu`s",40,812,paint);
                pdf.finishPage(page);
            }
            try(OutputStream out=new FileOutputStream(target)) {
                pdf.writeTo(out);
            }
        } finally {
            pdf.close();
        }
        launch(target,"application/pdf",true);
    }
    private void flatten(Object value,String prefix,List<String> lines)throws JSONException {
        if(value instanceof JSONObject) {
            JSONObject o=(JSONObject)value;
            Iterator<String> keys=o.keys();
            while(keys.hasNext()) {
                String k=keys.next();
                String lower=k.toLowerCase(Locale.ROOT);
                if(lower.contains("token")||lower.contains("password")||lower.contains("hash")||lower.equals("content"))continue;
                flatten(o.get(k),prefix.isEmpty()?label(k):prefix+" / "+label(k),lines);
            }
        } else if(value instanceof JSONArray) {
            JSONArray a=(JSONArray)value;
            for(int i=0; i<a.length(); i++)flatten(a.get(i),prefix+" "+(i+1),lines);
        } else lines.add(prefix+": "+(value==JSONObject.NULL?"—":String.valueOf(value)));
    }
    private String label(String s) {
        return s.replaceAll("([a-z])([A-Z])","$1 $2").replace('_',' ');
    }
}
