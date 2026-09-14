package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.util.*;
@Component public class Commands {
    private final Db db;
    private final ObjectMapper mapper;
    public Commands(Db db,ObjectMapper mapper) {
        this.db=db;
        this.mapper=mapper;
    }
    private Object canonical(Object v) {
        if(v instanceof Map<?,?> m) {
            TreeMap<String,Object> result=new TreeMap<>();
            m.forEach((k,x)->result.put(k.toString(),canonical(x)));
            return result;
        }
        if(v instanceof List<?> l)return l.stream().map(this::canonical).toList();
        return v;
    }
    private String hash(Map<String,Object> body) {
        try {
            return Tokens.digest(mapper.writeValueAsString(canonical(body)));
        } catch(Exception e) {
            throw new IllegalArgumentException("Invalid request.");
        }
    }
    // Call under the tenant transaction lock. Keys are unique per tenant + actor, across operations.
    public Optional<UUID> previous(Account a,String operation,Map<String,Object> body) {
        UUID key=V.id(body,"requestKey");
        var old=db.find("select operation,fingerprint,result_id from ap_command where tenant_id=? and actor_id=? and request_key=?",a.tenantId(),a.id(),key);
        if(old.isEmpty())return Optional.empty();
        if(!operation.equals(old.get().get("operation"))||!hash(body).equals(old.get().get("fingerprint")))throw ApiError.conflict("Request key was already used for different details.");
        return Optional.of((UUID)old.get().get("resultId"));
    }
    public UUID remember(Account a,String operation,Map<String,Object> body,UUID result) {
        db.update("insert into ap_command(tenant_id,actor_id,request_key,operation,fingerprint,result_id) values(?,?,?,?,?,?)",a.tenantId(),a.id(),V.id(body,"requestKey"),operation,hash(body),result);
        return result;
    }
    public void flat(Account a,UUID id) {
        db.one("select id from ap_flat where tenant_id=? and id=?",a.tenantId(),id);
    }
    public void vendor(Account a,UUID id) {
        if(id!=null)db.one("select id from ap_contact where tenant_id=? and id=? and active=true",a.tenantId(),id);
    }
    public Map<String,Object> settings(Account a) {
        db.update("insert into ap_settings(tenant_id) values(?) on conflict do nothing",a.tenantId());
        return db.one("select * from ap_settings where tenant_id=?",a.tenantId());
    }
    public void audit(Account a,String action,UUID id,String before,String after) {
        db.audit(a.tenantId(),a.id(),action,id,before,after);
    }
}
