package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Private local support records; creation does not claim external ticket delivery. */
@Service public class SupportService {
    private final Db db;
    public SupportService(Db db) {this.db=db;}
    public List<Map<String,Object>> mine(Account a) {
        return db.rows("select id,topic,message,status,created_at from ap_support_request where tenant_id=? and requester_id=? order by created_at desc limit 100",a.tenantId(),a.id());
    }
    @Transactional public Map<String,Object> create(Account a,Map<String,Object> p) {
        String topic=V.text(p,"topic",40),message=V.text(p,"message",1000);
        if(!Set.of("Payment approval","Account access","Flat mapping","Something else").contains(topic))throw new IllegalArgumentException("Choose a supported help topic.");
        UUID key=UUID.fromString(V.text(p,"requestKey",36));
        db.lockTenant(a.tenantId());
        var previous=db.find("select id,topic,message,status,created_at from ap_support_request where tenant_id=? and requester_id=? and request_key=?",a.tenantId(),a.id(),key);
        if(previous.isPresent()) {
            if(!topic.equals(previous.get().get("topic"))||!message.equals(previous.get().get("message")))throw ApiError.conflict("This submission key was already used for a different request.");
            return previous.get();
        }
        UUID id=UUID.randomUUID();
        db.update("insert into ap_support_request(id,tenant_id,requester_id,request_key,topic,message) values(?,?,?,?,?,?)",id,a.tenantId(),a.id(),key,topic,message);
        db.audit(a.tenantId(),a.actorId(),"SUPPORT_REQUEST_RECORDED",id,null,"Private local record; external delivery not configured");
        return db.one("select id,topic,message,status,created_at from ap_support_request where tenant_id=? and requester_id=? and id=?",a.tenantId(),a.id(),id);
    }
}
