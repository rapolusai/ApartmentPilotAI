package com.rapolus.apartmentpilotai.ui;
import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.rapolus.apartmentpilotai.data.AttachmentPolicy;
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
        byte[] bytes=attachmentBytes(data);
        String ext=AttachmentPolicy.extension(mime);
        File file=new File(folder(),"attachment-"+UUID.randomUUID()+ext);
        try(OutputStream out=new FileOutputStream(file)) {
            out.write(bytes);
        }
        launch(file,mime,false);
    }
    private byte[] attachmentBytes(JSONObject data)throws Exception {
        String encoded=data.getString("content");
        if(encoded.length()>7000000)throw new IllegalArgumentException("Attachment too large.");
        byte[] bytes=android.util.Base64.decode(encoded,android.util.Base64.DEFAULT);
        AttachmentPolicy.validate(bytes,data.getString("mime"));
        return bytes;
    }
    public Bitmap preview(JSONObject data)throws Exception {
        byte[] bytes=attachmentBytes(data);
        if(!data.getString("mime").equals("application/pdf")) {
            BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;
            BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
            options.inSampleSize=AttachmentPolicy.sample(options.outWidth,options.outHeight);options.inJustDecodeBounds=false;
            Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
            if(bitmap==null)throw new IOException("Image cannot be decoded.");
            return bitmap;
        }
        File file=new File(folder(),"preview-"+UUID.randomUUID()+".pdf");
        try {
            try(OutputStream out=new FileOutputStream(file)){out.write(bytes);}
            try(android.os.ParcelFileDescriptor fd=android.os.ParcelFileDescriptor.open(file,android.os.ParcelFileDescriptor.MODE_READ_ONLY);
                android.graphics.pdf.PdfRenderer renderer=new android.graphics.pdf.PdfRenderer(fd)) {
                if(renderer.getPageCount()==0)throw new IOException("PDF contains no pages.");
                try(android.graphics.pdf.PdfRenderer.Page page=renderer.openPage(0)) {
                    float scale=Math.min(1200f/page.getWidth(),1600f/page.getHeight());
                    Bitmap bitmap=Bitmap.createBitmap(Math.max(1,Math.round(page.getWidth()*scale)),Math.max(1,Math.round(page.getHeight()*scale)),Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(Color.WHITE);page.render(bitmap,null,null,android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);return bitmap;
                }
            }
        } finally {if(file.exists())file.delete();}
    }
    public void save(JSONObject data,Uri destination)throws Exception {
        byte[] bytes=attachmentBytes(data);
        try(OutputStream out=activity.getContentResolver().openOutputStream(destination,"w")) {
            if(out==null)throw new IOException("Cannot write to the selected destination.");
            out.write(bytes);out.flush();
        }
    }
    /** "Monthly summary · 2026-10" -> "Monthly-summary-2026-10_20261009-0215.csv", so shared files say what they are. */
    static String readableName(String title,String extension) {
        String base=title==null?"":title.replaceAll("[^A-Za-z0-9]+","-").replaceAll("^-+|-+$","");
        if(base.isEmpty())base="ApartmentCare";
        if(base.length()>60)base=base.substring(0,60);
        String stamp=java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Kolkata")).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss",java.util.Locale.ROOT));
        return base+"_"+stamp+extension;
    }
    public void exportCsv(String title,List<List<String>> rows)throws Exception {
        File file=new File(folder(),readableName(title,".csv"));
        try(Writer writer=new OutputStreamWriter(new FileOutputStream(file),java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write('\uFEFF');
            writer.write(com.rapolus.apartmentpilotai.data.CsvReport.encode(rows));
        }
        launch(file,"text/csv",true);
    }
    /** Any other record: its readable fields as a details list in the standard report layout. */
    public void exportPdf(String heading,JSONObject data)throws Exception {
        List<String> lines=new ArrayList<>();
        flatten(data,"",lines);
        List<String[]> rows=new ArrayList<>();
        for(String line:lines) {
            int split=line.indexOf(": ");
            rows.add(split<0?new String[]{line,""}:new String[]{line.substring(0,split),com.rapolus.apartmentpilotai.data.DateText.humanize(line.substring(split+2))});
        }
        if(rows.isEmpty())rows.add(new String[]{"Entries","None"});
        String apartment=data.optString("apartmentName","");
        exportReport(new ReportPdf.Doc(apartment,heading,"","").details(rows));
    }
    /** Draws a report in the standard layout and opens the Android share sheet. */
    public void exportReport(ReportPdf.Doc doc)throws Exception {
        File target=new File(folder(),readableName(doc.fileTitle(),".pdf"));
        ReportPdf.write(doc,com.rapolus.apartmentpilotai.data.DateText.format(java.time.Instant.now()),target);
        launch(target,"application/pdf",true);
    }
    private void flatten(Object value,String prefix,List<String> lines)throws JSONException {
        if(value instanceof JSONObject) {
            JSONObject o=(JSONObject)value;
            Iterator<String> keys=o.keys();
            while(keys.hasNext()) {
                String k=keys.next();
                String lower=k.toLowerCase(Locale.ROOT);
                if(lower.contains("token")||lower.contains("password")||lower.contains("hash")||lower.equals("content")||lower.equals("id")||lower.endsWith("id")&&!lower.equals("paid"))continue;
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
