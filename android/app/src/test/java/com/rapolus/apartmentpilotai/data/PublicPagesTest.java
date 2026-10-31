package com.rapolus.apartmentpilotai.data;
import static org.junit.Assert.*;
import org.junit.Test;
public class PublicPagesTest {
    @Test public void stripsTheApiPathFromTheBase() {
        assertEquals("http://127.0.0.1:8080/privacy",PublicPages.privacy("http://127.0.0.1:8080/api/v1"));
        assertEquals("https://api.example.in/delete-account",PublicPages.deleteAccount("https://api.example.in/api/v1/"));
    }
    @Test public void keepsABaseThatHasNoApiPath() {
        assertEquals("https://example.in/privacy",PublicPages.privacy("https://example.in/"));
        assertEquals("/privacy",PublicPages.privacy(null));
    }
}
