package in.schemesathi.auth;

import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List; import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final AppUserRepository users;
 public JwtAuthFilter(JwtService jwt,AppUserRepository users){this.jwt=jwt;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String header=req.getHeader("Authorization");
  if(header!=null&&header.startsWith("Bearer "))try{
   UUID id=UUID.fromString(jwt.subject(header.substring(7))); users.findById(id).ifPresent(u->{var auth=new UsernamePasswordAuthenticationToken(u.getId().toString(),null,List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole())));SecurityContextHolder.getContext().setAuthentication(auth);});
  }catch(RuntimeException ignored){SecurityContextHolder.clearContext();}
  chain.doFilter(req,res);
 }
}
