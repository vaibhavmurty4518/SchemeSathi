package in.schemesathi.documents;
import jakarta.validation.constraints.*;import java.time.Instant;import java.util.UUID;
public final class DocumentDtos {private DocumentDtos(){}
 public record CatalogResponse(UUID id,String code,String title,String description,String category,String issuingAuthority,String officialUrl) {public CatalogResponse(DocumentCatalogItem d){this(d.getId(),d.getCode(),d.getTitle(),d.getDescription(),d.getCategory(),d.getIssuingAuthority(),d.getOfficialUrl());}}
 public record UpsertRequest(@NotNull UUID documentId,@NotBlank @Pattern(regexp="NEEDED|READY|EXPIRED|NOT_APPLICABLE") String status,@Size(max=500) String notes){}
 public record ChecklistResponse(UUID id,UUID documentId,String code,String title,String category,String status,String notes,Instant updatedAt){public ChecklistResponse(CitizenDocumentChecklist c){this(c.getId(),c.getDocument().getId(),c.getDocument().getCode(),c.getDocument().getTitle(),c.getDocument().getCategory(),c.getStatus(),c.getNotes(),c.getUpdatedAt());}}
}
