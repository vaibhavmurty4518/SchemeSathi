package in.schemesathi.tracker;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.UUID;
public class ApplicationTrackerDtos {
  public record CreateRequest(@NotNull UUID schemeId, @Size(max=2000) String notes) {}
  public record UpdateRequest(@NotBlank @Pattern(regexp="INTERESTED|PREPARING|SUBMITTED|UNDER_REVIEW|APPROVED|REJECTED|ON_HOLD") String status, @Size(max=2000) String notes, LocalDate appliedOn) {}
  public record Response(UUID id, UUID schemeId, String status, String notes, LocalDate appliedOn, Instant createdAt, Instant lastUpdatedAt) {
    static Response from(CitizenSchemeApplication a){return new Response(a.getId(),a.getSchemeId(),a.getStatus(),a.getNotes(),a.getAppliedOn(),a.getCreatedAt(),a.getLastUpdatedAt());}
  }
}
