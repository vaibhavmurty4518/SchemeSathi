package in.schemesathi.eligibility;
import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.UUID;
@RestController @RequestMapping("/api/v1/eligibility")public class EligibilityController{private final EligibilityService service;public EligibilityController(EligibilityService service){this.service=service;}@GetMapping("/{schemeId}")public EligibilityDtos.Assessment assess(Authentication a,@PathVariable UUID schemeId){return service.assess(UUID.fromString(a.getName()),schemeId);}}
