package in.schemesathi.tracker;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "citizen_scheme_applications", uniqueConstraints = @UniqueConstraint(name="citizen_scheme_applications_user_scheme_unique", columnNames={"user_id","scheme_id"}))
public class CitizenSchemeApplication {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
  @Column(name="user_id", nullable=false) private UUID userId;
  @Column(name="scheme_id", nullable=false) private UUID schemeId;
  @Column(nullable=false, length=24) private String status="INTERESTED";
  @Column(length=2000) private String notes;
  @Column(name="applied_on") private LocalDate appliedOn;
  @Column(name="last_updated_at", nullable=false) private Instant lastUpdatedAt=Instant.now();
  @Column(name="created_at", nullable=false) private Instant createdAt=Instant.now();
  protected CitizenSchemeApplication() {}
  public CitizenSchemeApplication(UUID userId, UUID schemeId, String notes) { this.userId=userId; this.schemeId=schemeId; this.notes=notes; }
  public UUID getId(){return id;} public UUID getUserId(){return userId;} public UUID getSchemeId(){return schemeId;} public String getStatus(){return status;} public String getNotes(){return notes;} public LocalDate getAppliedOn(){return appliedOn;} public Instant getLastUpdatedAt(){return lastUpdatedAt;} public Instant getCreatedAt(){return createdAt;}
  public void update(String status, String notes, LocalDate appliedOn){this.status=status;this.notes=notes;this.appliedOn=appliedOn;this.lastUpdatedAt=Instant.now();}
}
