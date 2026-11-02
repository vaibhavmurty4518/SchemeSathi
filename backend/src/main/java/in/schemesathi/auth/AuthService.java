package in.schemesathi.auth;
import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service;
@Service public class AuthService{
 private final AppUserRepository repo;private final PasswordEncoder encoder;private final JwtService jwt;
 public AuthService(AppUserRepository repo,PasswordEncoder encoder,JwtService jwt){this.repo=repo;this.encoder=encoder;this.jwt=jwt;}
 public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest r){if(repo.existsByEmailIgnoreCase(r.email()))throw new IllegalArgumentException("An account with this email already exists");AppUser u=repo.save(new AppUser(r.email(),encoder.encode(r.password()),r.fullName()));return new AuthDtos.AuthResponse(jwt.issue(u),u);}
 public AuthDtos.AuthResponse login(AuthDtos.LoginRequest r){AppUser u=repo.findByEmailIgnoreCase(r.email()).orElseThrow(()->new IllegalArgumentException("Invalid email or password"));if(!encoder.matches(r.password(),u.getPasswordHash()))throw new IllegalArgumentException("Invalid email or password");return new AuthDtos.AuthResponse(jwt.issue(u),u);}
}
