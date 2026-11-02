package in.schemesathi.scheme;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="scheme_review_audit") public class SchemeReviewAudit {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(name="scheme_id",nullable=false) private UUID schemeId; @Column(name="reviewer_id",nullable=false) private String reviewerId; @Column(nullable=false,length=20) private String decision; @Column(columnDefinition="text") private String comment; @Column(name="decided_at",nullable=false) private Instant decidedAt; protected SchemeReviewAudit(){} public SchemeReviewAudit(UUID schemeId,String reviewerId,String decision,String comment,Instant decidedAt){this.schemeId=schemeId;this.reviewerId=reviewerId;this.decision=decision;this.comment=comment;this.decidedAt=decidedAt;}
}
