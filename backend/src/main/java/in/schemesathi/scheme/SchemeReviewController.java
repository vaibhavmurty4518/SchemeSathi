package in.schemesathi.scheme;
import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/admin/scheme-reviews") @PreAuthorize("hasAnyRole('ADMIN','REVIEWER')") public class SchemeReviewController {
 private final SchemeReviewService service; public SchemeReviewController(SchemeReviewService service){this.service=service;}
 @GetMapping("/queue") public List<SchemeDtos.SchemeResponse> queue(){return service.queue();}
 @PostMapping("/{id}/approve") public SchemeReviewDtos.ReviewResponse approve(@PathVariable UUID id,@RequestBody(required=false) SchemeReviewDtos.ReviewRequest req,org.springframework.security.core.Authentication auth){return service.decide(id,auth.getName(),req==null?null:req.comment(),true);}
 @PostMapping("/{id}/reject") public SchemeReviewDtos.ReviewResponse reject(@PathVariable UUID id,@RequestBody(required=false) SchemeReviewDtos.ReviewRequest req,org.springframework.security.core.Authentication auth){return service.decide(id,auth.getName(),req==null?null:req.comment(),false);}
}
