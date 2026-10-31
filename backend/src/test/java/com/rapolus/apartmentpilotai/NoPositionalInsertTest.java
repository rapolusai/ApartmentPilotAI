package com.rapolus.apartmentpilotai;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
/** Every INSERT must name its columns, so a later additive migration cannot silently shift values into the wrong column. */
final class NoPositionalInsertTest {
    private static final Pattern POSITIONAL=Pattern.compile("(?i)insert\\s+into\\s+[a-z_][a-z0-9_.]*\\s+values\\s*\\(");
    static List<String> offenders(Path root) throws Exception {
        List<String> found=new ArrayList<>();
        try(Stream<Path> files=Files.walk(root)) {
            for(Path file:files.filter(p->p.toString().endsWith(".java")).toList()) {
                List<String> lines=Files.readAllLines(file);
                for(int i=0; i<lines.size(); i++)if(POSITIONAL.matcher(lines.get(i)).find())found.add(root.relativize(file)+":"+(i+1));
            }
        }
        return found;
    }
    @Test void mainSourcesNameEveryInsertColumn() throws Exception {
        Path root=Path.of("src/main/java");
        assertTrue(Files.isDirectory(root),"Run from the backend module directory");
        assertEquals(List.of(),offenders(root));
    }
    @Test void scannerDetectsAPositionalInsert(@org.junit.jupiter.api.io.TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("Bad.java"),"class Bad { String s=\"insert into ap_ticket values(?,?)\"; String ok=\"insert into ap_ticket(id) values(?)\"; }");
        assertEquals(List.of("Bad.java:1"),offenders(dir));
    }
}
