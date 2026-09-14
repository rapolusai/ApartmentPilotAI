package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.security.*;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.finance.FinanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.security.MessageDigest;
@Service public class FileService {
    private final Db db;
    private final Commands c;
    private final TicketService tickets;
    private final NoticeService notices;
    private final FinanceService finance;
    public FileService(Db db,Commands c,TicketService tickets,NoticeService notices,FinanceService finance) {
        this.db=db;
        this.c=c;
        this.tickets=tickets;
        this.notices=notices;
        this.finance=finance;
    }
    public List<Map<String,Object>> documents(Account a) {
        return db.rows("select id,title,category,public,archived,created_at from ap_document where tenant_id=?"+(a.staff()?"":" and public=true and archived=false")+" order by created_at desc",a.tenantId());
    }
    public Map<String,Object> document(Account a,UUID id) {
        var d=db.one("select id,title,category,public,archived,created_at from ap_document where tenant_id=? and id=?",a.tenantId(),id);
        if(!a.staff()&&(!Boolean.TRUE.equals(d.get("public"))||Boolean.TRUE.equals(d.get("archived"))))throw ApiError.forbidden();
        return d;
    }
    @Transactional public Object saveDocument(Account a,UUID id,Map<String,Object> p) {
        a.requireStaff();
        db.lockTenant(a.tenantId());
        var old=c.previous(a,"DOCUMENT",p);
        if(old.isPresent())return Map.of("id",old.get());
        String title=V.text(p,"title",100),category=V.text(p,"category",40);
        if(id==null) {
            id=UUID.randomUUID();
            db.update("insert into ap_document(id,tenant_id,title,category,public,created_by) values(?,?,?,?,?,?)",id,a.tenantId(),title,category,V.bool(p,"public",false),a.id());
        } else {
            document(a,id);
            db.update("update ap_document set title=?,category=?,public=?,archived=? where tenant_id=? and id=?",title,category,V.bool(p,"public",false),V.bool(p,"archived",false),a.tenantId(),id);
        }
        c.remember(a,"DOCUMENT",p,id);
        c.audit(a,"DOCUMENT_SAVED",id,null,title);
        return Map.of("id",id);
    }
    private void access(Account a,String kind,UUID id,boolean write) {
        switch(kind) {
            case "DOCUMENT"-> {
                document(a,id);
                if(write)a.requireStaff();
            }
            case "TICKET"-> {
                var t=tickets.get(a,id);
                if(write&&!a.staff()&&!a.id().equals(t.get("createdBy")))throw ApiError.forbidden();
            }
            case "NOTICE"-> {
                var n=notices.get(a,id);
                if(write) {
                    a.requireStaff();
                    if(!n.get("status").equals("DRAFT"))throw ApiError.conflict("Attach files before publishing.");
                }
            }
            case "PAYMENT"-> {
                var p=db.one("select p.status,p.submitted_by,p.bill_id from ap_payment p where p.tenant_id=? and p.id=?",a.tenantId(),id);
                finance.bill(a,(UUID)p.get("billId"));
                if(write&&(!p.get("status").equals("PENDING")||!a.staff()&&!a.id().equals(p.get("submittedBy"))))throw ApiError.forbidden();
            }
            case "EXPENSE"-> {
                var e=db.one("select public,paid from ap_expense where tenant_id=? and id=?",a.tenantId(),id);
                if(write)a.requireStaff();
                else if(!a.staff()&&(!Boolean.TRUE.equals(e.get("public"))||!Boolean.TRUE.equals(e.get("paid"))||!Boolean.TRUE.equals(c.settings(a).get("expensesVisible"))))throw ApiError.forbidden();
            }
            default->throw new IllegalArgumentException("Unsupported attachment type.");
        }
    }
    public List<Map<String,Object>> list(Account a,String kind,UUID id) {
        access(a,kind,id,false);
        return db.rows("select id,name,mime,octet_length(bytes) as size,created_at from ap_file where tenant_id=? and parent_kind=? and parent_id=? order by created_at",a.tenantId(),kind,id);
    }
    @Transactional public Object upload(Account a,Map<String,Object> p) {
        db.lockTenant(a.tenantId());
        String kind=V.choice(p,"kind","DOCUMENT","TICKET","NOTICE","PAYMENT","EXPENSE");
        UUID parent=V.id(p,"parentId");
        access(a,kind,parent,true);
        String name=V.text(p,"name",120).replaceAll("[\\\\/\\p{Cntrl}]","_");
        String encoded=V.text(p,"content",7000000);
        byte[] data;
        try {
            data=Base64.getDecoder().decode(encoded);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid file encoding.");
        }
        String mime=sniff(data);
        if(data.length>5242880)throw new IllegalArgumentException("File limit is 5 MB.");
        String sha;
        try {
            sha=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
        var old=db.find("select id from ap_file where tenant_id=? and parent_kind=? and parent_id=? and sha256=?",a.tenantId(),kind,parent,sha);
        if(old.isPresent())return Map.of("id",old.get().get("id"));
        if(db.count("select count(*) from ap_file where tenant_id=? and parent_kind=? and parent_id=?",a.tenantId(),kind,parent)>=5)throw ApiError.conflict("A record supports up to five attachments.");
        UUID id=UUID.randomUUID();
        db.update("insert into ap_file(id,tenant_id,parent_kind,parent_id,name,mime,bytes,sha256,created_by) values(?,?,?,?,?,?,?,?,?)",id,a.tenantId(),kind,parent,name,mime,data,sha,a.id());
        c.audit(a,"ATTACHMENT_UPLOADED",id,null,name);
        return Map.of("id",id,"mime",mime);
    }
    public static String sniff(byte[] b) {
        if(b==null||b.length<8)throw new IllegalArgumentException("Choose a PNG, JPEG or PDF file.");
        if((b[0]&255)==137&&b[1]==80&&b[2]==78&&b[3]==71&&b[4]==13&&b[5]==10&&b[6]==26&&b[7]==10)return "image/png";
        if((b[0]&255)==255&&(b[1]&255)==216&&(b[2]&255)==255)return "image/jpeg";
        if(b[0]==37&&b[1]==80&&b[2]==68&&b[3]==70&&b[4]==45)return "application/pdf";
        throw new IllegalArgumentException("Only PNG, JPEG and PDF contents are supported.");
    }
    public Map<String,Object> content(Account a,UUID id) {
        var f=db.one("select * from ap_file where tenant_id=? and id=?",a.tenantId(),id);
        access(a,f.get("parentKind").toString(),(UUID)f.get("parentId"),false);
        return Map.of("id",id,"name",f.get("name"),"mime",f.get("mime"),"content",Base64.getEncoder().encodeToString((byte[])f.get("bytes")));
    }
}
