package in.schemesathi.tracker;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/v1/my-applications")
public class ApplicationTrackerController {
  private final ApplicationTrackerService service;
  public ApplicationTrackerController(ApplicationTrackerService service){this.service=service;}
  private UUID userId(Authentication auth){return UUID.fromString(auth.getName());}
  @GetMapping public List<ApplicationTrackerDtos.Response> list(Authentication auth){return service.list(userId(auth));}
  @PostMapping public ApplicationTrackerDtos.Response create(Authentication auth,@Valid @RequestBody ApplicationTrackerDtos.CreateRequest request){return service.create(userId(auth),request);}
  @PutMapping("/{id}") public ApplicationTrackerDtos.Response update(Authentication auth,@PathVariable UUID id,@Valid @RequestBody ApplicationTrackerDtos.UpdateRequest request){return service.update(userId(auth),id,request);}
  @DeleteMapping("/{id}") public void delete(Authentication auth,@PathVariable UUID id){service.delete(userId(auth),id);}
}
