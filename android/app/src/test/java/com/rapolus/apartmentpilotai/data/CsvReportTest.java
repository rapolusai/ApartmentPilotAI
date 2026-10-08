package com.rapolus.apartmentpilotai.data;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
public class CsvReportTest {
    @Test public void quotesCommasQuotesAndNewlines() {
        assertEquals("\"A, B\",\"Say \"\"yes\"\"\",\"line1\nline2\"\r\n",CsvReport.encode(List.of(List.of("A, B","Say \"yes\"","line1\nline2"))));
    }
    @Test public void neutralisesFormulasIncludingLeadingWhitespace() {
        for(String input:List.of("=1+1","+SUM(A1)","-1+2","@A1","  =1","\t=1","\r=1","\n=1"))
            assertTrue(input,CsvReport.encode(List.of(List.of(input))).startsWith("\"'"));
    }
    @Test public void preservesOrdinaryAmountsAndEmptyCells() {
        assertEquals("\"250.00\",\"\",\"\"\r\n",CsvReport.encode(List.of(Arrays.asList("250.00",null,""))));
        assertEquals("",CsvReport.encode(List.of()));
    }
}
