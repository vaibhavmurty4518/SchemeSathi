package in.schemesathi.documents;
import jakarta.validation.Valid;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1/documents") public class DocumentController {
 private final DocumentService service;public DocumentController(DocumentService service){this.service=service;}
 @GetMapping("/catalog") public List<DocumentDtos.CatalogResponse> catalog(){return service.catalog();}
 @GetMapping("/my-checklist") public List<DocumentDtos.ChecklistResponse> list(Authentication a){return service.list(UUID.fromString(a.getName()));}
 @PutMapping("/my-checklist") public DocumentDtos.ChecklistResponse upsert(Authentication a,@Valid @RequestBody DocumentDtos.UpsertRequest r){return service.upsert(UUID.fromString(a.getName()),r);}
 @DeleteMapping("/my-checklist/{id}") public void delete(Authentication a,@PathVariable UUID id){service.delete(UUID.fromString(a.getName()),id);}
}
