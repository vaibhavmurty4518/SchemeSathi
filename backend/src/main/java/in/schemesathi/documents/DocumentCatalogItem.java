package in.schemesathi.documents;
import jakarta.persistence.*;import java.util.UUID;
@Entity @Table(name="document_catalog")
public class DocumentCatalogItem {
 @Id private UUID id; @Column(nullable=false,unique=true,length=60) private String code;
 @Column(nullable=false,length=180) private String title; @Column(nullable=false) private String description;
 @Column(nullable=false,length=60) private String category; @Column(name="issuing_authority",length=180) private String issuingAuthority;
 @Column(name="official_url",length=1000) private String officialUrl; @Column(nullable=false) private boolean active;
 protected DocumentCatalogItem(){}
 public UUID getId(){return id;} public String getCode(){return code;} public String getTitle(){return title;} public String getDescription(){return description;} public String getCategory(){return category;} public String getIssuingAuthority(){return issuingAuthority;} public String getOfficialUrl(){return officialUrl;} public boolean isActive(){return active;}
}
