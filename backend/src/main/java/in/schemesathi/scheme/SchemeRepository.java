package in.schemesathi.scheme;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface SchemeRepository extends JpaRepository<GovernmentScheme,UUID>{}
