package in.schemesathi.scheme;
import java.time.Instant; import java.util.UUID;
public class SchemeReviewDtos {
 public record ReviewRequest(String comment) {}
 public record ReviewResponse(UUID schemeId,String reviewStatus,boolean published,Instant verifiedAt,String reviewerId,String comment) {}
}
