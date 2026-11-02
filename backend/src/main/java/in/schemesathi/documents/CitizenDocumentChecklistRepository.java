package in.schemesathi.documents;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface CitizenDocumentChecklistRepository extends JpaRepository<CitizenDocumentChecklist,UUID>{List<CitizenDocumentChecklist> findByUserIdOrderByUpdatedAtDesc(UUID userId);Optional<CitizenDocumentChecklist> findByIdAndUserId(UUID id,UUID userId);Optional<CitizenDocumentChecklist> findByUserIdAndDocumentId(UUID userId,UUID documentId);}
