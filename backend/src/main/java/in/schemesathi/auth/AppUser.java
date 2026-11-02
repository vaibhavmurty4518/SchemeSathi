package in.schemesathi.auth;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="app_users")
public class AppUser {
  @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
  @Column(nullable=false, unique=true, length=320) private String email;
  @Column(name="password_hash", nullable=false) private String passwordHash;
  @Column(nullable=false, length=80) private String fullName;
  @Column(nullable=false, length=20) private String role="CITIZEN";
  @Column(name="created_at", nullable=false) private Instant createdAt=Instant.now();
  protected AppUser() {}
  public AppUser(String email,String passwordHash,String fullName){this.email=email.toLowerCase().trim();this.passwordHash=passwordHash;this.fullName=fullName.trim();}
  public UUID getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getFullName(){return fullName;} public String getRole(){return role;}
}
