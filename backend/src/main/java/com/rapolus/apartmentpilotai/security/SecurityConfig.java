package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.store.Db;
import com.rapolus.apartmentpilotai.operations.SubscriptionService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Configuration public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean SecurityFilterChain security(HttpSecurity http,Db db,SubscriptionService subscriptions) throws Exception {
        http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) .authorizeHttpRequests(a->a.requestMatchers("/actuator/health","/api/v1/status","/api/v1/auth/**","/api/v1/provider/**").permitAll().anyRequest().authenticated()) .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->json401(res))) .addFilterBefore(new ActivityLoggingFilter(),UsernamePasswordAuthenticationFilter.class).addFilterBefore(new BearerFilter(db,subscriptions),UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    static void json401(HttpServletResponse r) throws IOException {
        r.setStatus(401);
        r.setContentType("application/json");
        r.getWriter().write("{\"message\":\"Sign in again. Your session has expired or is invalid.\"}");
    }
    static final class ActivityLoggingFilter extends OncePerRequestFilter {
        private static final Logger LOG=LoggerFactory.getLogger(ActivityLoggingFilter.class);
        @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException {
            long started=System.nanoTime();
            String supplied=req.getHeader("X-Request-ID");
            String traceId=supplied!=null&&supplied.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")?supplied:UUID.randomUUID().toString();
            res.setHeader("X-Request-ID",traceId);
            boolean failed=false;
            try {
                chain.doFilter(req,res);
            } catch(ServletException|IOException|RuntimeException failure) {
                failed=true;
                throw failure;
            } finally {
                var auth=SecurityContextHolder.getContext().getAuthentication();
                String actor=auth==null?"anonymous":auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("authenticated");
                long elapsed=(System.nanoTime()-started)/1_000_000L;
                String path=req.getRequestURI().replaceAll("[\\r\\n]","");
                if(path.startsWith("/api/v1/auth/invites/"))path="/api/v1/auth/invites/{code}";
                path=path.replaceAll("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}","{id}");
                int status=failed?500:res.getStatus();
                String message="activity trace="+traceId+" method="+req.getMethod()+" path="+path+" status="+status+" actor="+actor+" durationMs="+elapsed;
                if(status>=400)LOG.warn(message); else LOG.info(message);
            }
        }
    }
    static final class BearerFilter extends OncePerRequestFilter {
        private final Db db;
        private final SubscriptionService subscriptions;
        BearerFilter(Db db,SubscriptionService subscriptions) {
            this.db=db;
            this.subscriptions=subscriptions;
        }
        @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException {
            String header=req.getHeader("Authorization");
            if(header!=null && header.startsWith("Bearer ")) {
                String token=header.substring(7);
                var row=db.find("select u.id,u.tenant_id,u.flat_id,u.name,u.role from ap_session s join ap_user u on u.id=s.user_id where s.token_hash=? and s.expires_at>now() and u.status='ACTIVE'",Tokens.digest(token));
                // An expired or unknown token stays unauthenticated: protected routes still receive the same JSON 401 from
                // the entry point, while public routes such as login and status keep working for a client holding a stale token.
                if(row.isEmpty()) {
                    chain.doFilter(req,res);
                    return;
                }
                var u=row.get();
                Account a=new Account(UUID.fromString(u.get("id").toString()),UUID.fromString(u.get("tenantId").toString()),u.get("flatId")==null?null:UUID.fromString(u.get("flatId").toString()),u.get("name").toString(),u.get("role").toString());
                var auth=new UsernamePasswordAuthenticationToken(a,null,List.of(new SimpleGrantedAuthority("ROLE_"+a.role())));
                SecurityContextHolder.getContext().setAuthentication(auth);
                if(!subscriptions.entitlement(a.tenantId()).allowed()&&!subscriptionManagementAllowed(req,a)) {
                    res.setStatus(402);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.setHeader("Cache-Control","no-store");
                    res.getWriter().write("{\"code\":\"SUBSCRIPTION_REQUIRED\",\"message\":\"Subscription required. Your apartment trial has ended. Ask the apartment Admin to subscribe to restore access.\"}");
                    return;
                }
            }
            chain.doFilter(req,res);
        }
        private boolean subscriptionManagementAllowed(HttpServletRequest req,Account account) {
            String path=req.getRequestURI();
            if("POST".equals(req.getMethod())&&"/api/v1/logout".equals(path))return true;
            if(!account.role().equals("ADMIN"))return false;
            return "GET".equals(req.getMethod())&&"/api/v1/ops/subscription".equals(path)
                ||"POST".equals(req.getMethod())&&Set.of("/api/v1/ops/subscription/offer","/api/v1/ops/subscription/install","/api/v1/ops/referrals/claim").contains(path);
        }
    }
}
