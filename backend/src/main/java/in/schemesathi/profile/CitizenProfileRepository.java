package in.schemesathi.profile;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface CitizenProfileRepository extends JpaRepository<CitizenProfile,UUID>{}
