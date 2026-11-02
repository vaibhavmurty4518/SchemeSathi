package in.schemesathi.documents;
import jakarta.persistence.*;import java.time.Instant;import java.util.UUID;
@Entity @Table(name="citizen_document_checklist",uniqueConstraints=@UniqueConstraint(columnNames={"user_id","document_id"}))
public class CitizenDocumentChecklist {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(name="user_id",nullable=false) private UUID userId;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="document_id") private DocumentCatalogItem document;
 @Column(nullable=false,length=20) private String status="NEEDED";
 @Column(length=500) private String notes;
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected CitizenDocumentChecklist(){}
 public CitizenDocumentChecklist(UUID userId,DocumentCatalogItem document,String status,String notes){this.userId=userId;this.document=document;this.status=status;this.notes=notes;}
 public UUID getId(){return id;} public UUID getUserId(){return userId;} public DocumentCatalogItem getDocument(){return document;} public String getStatus(){return status;} public String getNotes(){return notes;} public Instant getUpdatedAt(){return updatedAt;}
 public void update(String status,String notes){this.status=status;this.notes=notes;this.updatedAt=Instant.now();}
}
