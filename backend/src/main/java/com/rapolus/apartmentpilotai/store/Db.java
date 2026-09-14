package com.rapolus.apartmentpilotai.store;
import com.rapolus.apartmentpilotai.api.ApiError;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component public class Db {
    private final JdbcTemplate jdbc;
    public Db(JdbcTemplate jdbc) {
        this.jdbc=jdbc;
    }
    public int update(String sql,Object... args) {
        return jdbc.update(sql,args);
    }
    public List<Map<String,Object>> rows(String sql,Object... args) {
        return jdbc.query(sql,(rs,n)-> {
            Map<String,Object> row=new LinkedHashMap<>();
            ResultSetMetaData md=rs.getMetaData();
            for(int i=1; i<=md.getColumnCount(); i++) {
                String raw=md.getColumnLabel(i);
                StringBuilder k=new StringBuilder();
                boolean cap=false;
                for(char c:raw.toCharArray()) {
                    if(c=='_') {
                        cap=true;
                        continue;
                    }
                    k.append(cap?Character.toUpperCase(c):c);
                    cap=false;
                }
                Object v=rs.getObject(i);
                if(v instanceof java.sql.Date d)v=d.toLocalDate().toString();
                if(v instanceof Timestamp t)v=t.toInstant().toString();
                row.put(k.toString(),v);
            }
            return row;
        }
        ,args);
    }
    public Optional<Map<String,Object>> find(String sql,Object... args) {
        return rows(sql,args).stream().findFirst();
    }
    public Map<String,Object> one(String sql,Object... args) {
        return find(sql,args).orElseThrow(ApiError::missing);
    }
    public BigDecimal decimal(String sql,Object... args) {
        return jdbc.queryForObject(sql,BigDecimal.class,args);
    }
    public long count(String sql,Object... args) {
        Long n=jdbc.queryForObject(sql,Long.class,args);
        return n==null?0:n;
    }
    public void lockTenant(UUID id) {
        one("select id from ap_tenant where id=? for update",id);
    }
    public void audit(UUID tenant,UUID actor,String action,Object id,String before,String after) {
        update("insert into ap_audit(id,tenant_id,actor_id,action,record_id,before_value,after_value) values(?,?,?,?,?,?,?)",UUID.randomUUID(),tenant,actor,action,String.valueOf(id),before,after);
    }
    public void notify(UUID tenant,UUID to,String key,String title,String body,String target) {
        update("insert into ap_inbox(id,tenant_id,recipient_id,event_key,title,body,target) values(?,?,?,?,?,?,?) on conflict(recipient_id,event_key) do nothing",UUID.randomUUID(),tenant,to,key,title,body,target);
    }
    public void notifyStaff(UUID tenant,String key,String title,String body,String target) {
        rows("select id from ap_user where tenant_id=? and status='ACTIVE' and role in ('ADMIN','TREASURER')",tenant) .forEach(u->notify(tenant,UUID.fromString(u.get("id").toString()),key,title,body,target));
    }
}
