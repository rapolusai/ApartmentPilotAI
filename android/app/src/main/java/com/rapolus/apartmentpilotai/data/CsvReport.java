package com.rapolus.apartmentpilotai.data;

import java.util.List;

/** RFC-style quoted CSV with spreadsheet-formula injection protection. */
public final class CsvReport {
    private CsvReport() {}
    public static String encode(List<List<String>> rows) {
        StringBuilder out=new StringBuilder();
        for(List<String> row:rows) {
            for(int i=0;i<row.size();i++) {
                if(i>0)out.append(',');
                String value=row.get(i)==null?"":row.get(i);
                int start=0;
                while(start<value.length()&&Character.isWhitespace(value.charAt(start)))start++;
                if((start<value.length()&&"=+-@".indexOf(value.charAt(start))>=0)||value.startsWith("\t")||value.startsWith("\r")||value.startsWith("\n"))value="'"+value;
                out.append('"').append(value.replace("\"","\"\"")).append('"');
            }
            out.append("\r\n");
        }
        return out.toString();
    }
}
