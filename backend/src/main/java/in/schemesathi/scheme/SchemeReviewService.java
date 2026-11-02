package in.schemesathi.scheme;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.Instant; import java.util.*;
@Service public class SchemeReviewService {
 private final SchemeRepository schemes; private final SchemeReviewRepository reviews;
 public SchemeReviewService(SchemeRepository schemes,SchemeReviewRepository reviews){this.schemes=schemes;this.reviews=reviews;}
 public List<SchemeDtos.SchemeResponse> queue(){return schemes.findAll().stream().filter(s->"IN_REVIEW".equals(s.getReviewStatus())).sorted(Comparator.comparing(GovernmentScheme::getTitle)).map(SchemeDtos.SchemeResponse::new).toList();}
 @Transactional public SchemeReviewDtos.ReviewResponse decide(UUID schemeId,String reviewerId,String comment,boolean approve){GovernmentScheme scheme=schemes.findById(schemeId).orElseThrow(()->new NoSuchElementException("Scheme not found")); if(!"IN_REVIEW".equals(scheme.getReviewStatus()))throw new IllegalStateException("Only schemes in IN_REVIEW can be decided"); Instant now=Instant.now(); scheme.setReviewStatus(approve?"APPROVED":"REJECTED"); scheme.setPublished(approve); if(approve)scheme.setVerifiedAt(now); schemes.save(scheme); SchemeReviewAudit audit=new SchemeReviewAudit(schemeId,reviewerId,approve?"APPROVED":"REJECTED",comment,now); reviews.save(audit); return new SchemeReviewDtos.ReviewResponse(schemeId,scheme.getReviewStatus(),scheme.isPublished(),scheme.getVerifiedAt(),reviewerId,comment);}
}
