package in.schemesathi.documents;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface DocumentCatalogRepository extends JpaRepository<DocumentCatalogItem,UUID>{List<DocumentCatalogItem> findByActiveTrueOrderByCategoryAscTitleAsc();}
