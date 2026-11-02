package in.schemesathi.tracker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CitizenSchemeApplicationRepository extends JpaRepository<CitizenSchemeApplication,UUID> {
  List<CitizenSchemeApplication> findByUserIdOrderByLastUpdatedAtDesc(UUID userId);
  Optional<CitizenSchemeApplication> findByIdAndUserId(UUID id, UUID userId);
  boolean existsByUserIdAndSchemeId(UUID userId, UUID schemeId);
}
