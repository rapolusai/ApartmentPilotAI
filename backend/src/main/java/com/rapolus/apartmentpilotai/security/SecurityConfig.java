package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.store.Db;
import java.io.IOException;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
@Configuration public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean SecurityFilterChain security(HttpSecurity http,Db db) throws Exception {
        http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) .authorizeHttpRequests(a->a.requestMatchers("/actuator/health","/api/v1/status","/api/v1/auth/**","/api/v1/provider/**").permitAll().anyRequest().authenticated()) .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->json401(res))) .addFilterBefore(new BearerFilter(db),UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    static void json401(HttpServletResponse r) throws IOException {
        r.setStatus(401);
        r.setContentType("application/json");
        r.getWriter().write("{\"message\":\"Sign in again. Your session has expired or is invalid.\"}");
    }
    static final class BearerFilter extends OncePerRequestFilter {
        private final Db db;
        BearerFilter(Db db) {
            this.db=db;
        }
        @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException {
            String header=req.getHeader("Authorization");
            if(header!=null && header.startsWith("Bearer ")) {
                String token=header.substring(7);
                var row=db.find("select u.id,u.tenant_id,u.flat_id,u.name,u.role from ap_session s join ap_user u on u.id=s.user_id where s.token_hash=? and s.expires_at>now() and u.status='ACTIVE'",Tokens.digest(token));
                if(row.isEmpty()) {
                    json401(res);
                    return;
                }
                var u=row.get();
                Account a=new Account(UUID.fromString(u.get("id").toString()),UUID.fromString(u.get("tenantId").toString()),u.get("flatId")==null?null:UUID.fromString(u.get("flatId").toString()),u.get("name").toString(),u.get("role").toString());
                var auth=new UsernamePasswordAuthenticationToken(a,null,List.of(new SimpleGrantedAuthority("ROLE_"+a.role())));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(req,res);
        }
    }
}
