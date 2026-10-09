package com.rapolus.apartmentpilotai.api;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
/** Errors that still reach the /error dispatch keep their real status and also carry the {"message":...} the app reads.
 * The text is the generic status phrase only; exception details stay hidden (server.error.include-message: never). */
@Component public class JsonErrorAttributes extends DefaultErrorAttributes {
    @Override public Map<String,Object> getErrorAttributes(WebRequest request,ErrorAttributeOptions options) {
        Map<String,Object> attributes=super.getErrorAttributes(request,options);
        Object status=attributes.get("status");
        HttpStatus resolved=status instanceof Integer code?HttpStatus.resolve(code):null;
        attributes.put("message",resolved==null?"Request could not be processed.":resolved.getReasonPhrase()+".");
        return attributes;
    }
}
