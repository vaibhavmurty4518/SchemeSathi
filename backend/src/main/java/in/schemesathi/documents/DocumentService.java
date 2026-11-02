package in.schemesathi.documents;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.util.*;
@Service public class DocumentService {
 private final DocumentCatalogRepository catalog;private final CitizenDocumentChecklistRepository checklist;
 public DocumentService(DocumentCatalogRepository catalog,CitizenDocumentChecklistRepository checklist){this.catalog=catalog;this.checklist=checklist;}
 public List<DocumentDtos.CatalogResponse> catalog(){return catalog.findByActiveTrueOrderByCategoryAscTitleAsc().stream().map(DocumentDtos.CatalogResponse::new).toList();}
 public List<DocumentDtos.ChecklistResponse> list(UUID user){return checklist.findByUserIdOrderByUpdatedAtDesc(user).stream().map(DocumentDtos.ChecklistResponse::new).toList();}
 @Transactional public DocumentDtos.ChecklistResponse upsert(UUID user,DocumentDtos.UpsertRequest req){var doc=catalog.findById(req.documentId()).filter(DocumentCatalogItem::isActive).orElseThrow(()->new NoSuchElementException("Document catalog item not found"));var row=checklist.findByUserIdAndDocumentId(user,doc.getId()).orElseGet(()->new CitizenDocumentChecklist(user,doc,req.status(),req.notes()));row.update(req.status(),req.notes());return new DocumentDtos.ChecklistResponse(checklist.save(row));}
 @Transactional public void delete(UUID user,UUID id){var row=checklist.findByIdAndUserId(id,user).orElseThrow(()->new NoSuchElementException("Checklist item not found"));checklist.delete(row);}
}
