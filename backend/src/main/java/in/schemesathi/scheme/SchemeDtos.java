package in.schemesathi.scheme;
import com.fasterxml.jackson.databind.JsonNode; import java.time.Instant; import java.util.UUID;
public class SchemeDtos {
 public record SchemeResponse(UUID id,String title,String summary,String scope,String stateCode,String category,String benefitDescription,JsonNode eligibilityRules,JsonNode requiredDocuments,String applicationUrl,String sourceUrl,String sourceName,Instant verifiedAt,String reviewStatus){public SchemeResponse(GovernmentScheme s){this(s.getId(),s.getTitle(),s.getSummary(),s.getScope(),s.getStateCode(),s.getCategory(),s.getBenefitDescription(),s.getEligibilityRules(),s.getRequiredDocuments(),s.getApplicationUrl(),s.getSourceUrl(),s.getSourceName(),s.getVerifiedAt(),s.getReviewStatus());}}
}
