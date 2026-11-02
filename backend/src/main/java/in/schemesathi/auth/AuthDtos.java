package in.schemesathi.auth;
import jakarta.validation.constraints.*;
public class AuthDtos {
 public record RegisterRequest(@NotBlank @Size(min=2,max=80) String fullName,@NotBlank @Email @Size(max=320) String email,@NotBlank @Size(min=10,max=72) String password){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record AuthResponse(String token,String tokenType,String fullName,String email,String role){ public AuthResponse(String token,AppUser u){this(token,"Bearer",u.getFullName(),u.getEmail(),u.getRole());}}
}
