package in.schemesathi.scheme;

import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="government_schemes")
public class GovernmentScheme {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false,length=200) private String title;
 @Column(nullable=false,columnDefinition="text") private String summary;
 @Column(nullable=false,length=20) private String scope;
 @Column(name="state_code",length=2) private String stateCode;
 @Column(nullable=false,length=80) private String category;
 @Column(name="benefit_description",columnDefinition="text") private String benefitDescription;
 @JdbcTypeCode(SqlTypes.JSON) @Column(name="eligibility_rules",columnDefinition="jsonb",nullable=false) private JsonNode eligibilityRules;
 @JdbcTypeCode(SqlTypes.JSON) @Column(name="required_documents",columnDefinition="jsonb",nullable=false) private JsonNode requiredDocuments;
 @Column(name="application_url",length=1000) private String applicationUrl;
 @Column(name="source_url",nullable=false,length=1000) private String sourceUrl;
 @Column(name="source_name",nullable=false,length=200) private String sourceName;
 @Column(name="verified_at") private Instant verifiedAt;
 @Column(name="review_status",nullable=false,length=20) private String reviewStatus="DRAFT";
 @Column(nullable=false) private boolean published=false;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 protected GovernmentScheme(){}
 public void setReviewStatus(String reviewStatus){this.reviewStatus=reviewStatus;}
 public void setPublished(boolean published){this.published=published;}
 public void setVerifiedAt(Instant verifiedAt){this.verifiedAt=verifiedAt;}
 public UUID getId(){return id;}public String getTitle(){return title;}public String getSummary(){return summary;}public String getScope(){return scope;}public String getStateCode(){return stateCode;}public String getCategory(){return category;}public String getBenefitDescription(){return benefitDescription;}public JsonNode getEligibilityRules(){return eligibilityRules;}public JsonNode getRequiredDocuments(){return requiredDocuments;}public String getApplicationUrl(){return applicationUrl;}public String getSourceUrl(){return sourceUrl;}public String getSourceName(){return sourceName;}public Instant getVerifiedAt(){return verifiedAt;}public String getReviewStatus(){return reviewStatus;}public boolean isPublished(){return published;}
}
