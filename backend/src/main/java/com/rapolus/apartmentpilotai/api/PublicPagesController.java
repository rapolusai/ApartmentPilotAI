package com.rapolus.apartmentpilotai.api;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
/**
 * The two pages Google Play requires outside the app: the privacy policy and a way to request account deletion
 * without the app. Plain HTML from the classpath; the contact address comes from SUPPORT_EMAIL, never from source.
 */
@RestController public class PublicPagesController {
    private final String supportEmail;
    public PublicPagesController(@Value("${app.support-email:}")String supportEmail) {
        this.supportEmail=supportEmail==null?"":supportEmail.trim();
    }
    @GetMapping(value="/privacy",produces=MediaType.TEXT_HTML_VALUE) String privacy() throws IOException {
        return page("pages/privacy.html");
    }
    @GetMapping(value="/delete-account",produces=MediaType.TEXT_HTML_VALUE) String deleteAccount() throws IOException {
        return page("pages/delete-account.html");
    }
    private String page(String path) throws IOException {
        String html=new String(new ClassPathResource(path).getInputStream().readAllBytes(),StandardCharsets.UTF_8);
        String contact=supportEmail.isEmpty()?"the developer contact address shown on the Google Play listing":"<a href=\"mailto:"+escape(supportEmail)+"\">"+escape(supportEmail)+"</a>";
        return html.replace("{{CONTACT}}",contact);
    }
    static String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}
