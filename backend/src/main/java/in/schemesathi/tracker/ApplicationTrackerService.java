package in.schemesathi.tracker;

import in.schemesathi.scheme.SchemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ApplicationTrackerService {
  private final CitizenSchemeApplicationRepository applications;
  private final SchemeRepository schemes;
  public ApplicationTrackerService(CitizenSchemeApplicationRepository applications, SchemeRepository schemes){this.applications=applications;this.schemes=schemes;}
  public List<ApplicationTrackerDtos.Response> list(UUID userId){return applications.findByUserIdOrderByLastUpdatedAtDesc(userId).stream().map(ApplicationTrackerDtos.Response::from).toList();}
  @Transactional public ApplicationTrackerDtos.Response create(UUID userId, ApplicationTrackerDtos.CreateRequest request){
    if(!schemes.existsById(request.schemeId())) throw new NoSuchElementException("Scheme not found");
    if(applications.existsByUserIdAndSchemeId(userId,request.schemeId())) throw new IllegalArgumentException("This scheme is already in your tracker");
    return ApplicationTrackerDtos.Response.from(applications.save(new CitizenSchemeApplication(userId,request.schemeId(),request.notes())));
  }
  @Transactional public ApplicationTrackerDtos.Response update(UUID userId, UUID id, ApplicationTrackerDtos.UpdateRequest request){
    CitizenSchemeApplication a=applications.findByIdAndUserId(id,userId).orElseThrow(()->new NoSuchElementException("Tracker entry not found"));
    a.update(request.status(),request.notes(),request.appliedOn()); return ApplicationTrackerDtos.Response.from(a);
  }
  @Transactional public void delete(UUID userId, UUID id){CitizenSchemeApplication a=applications.findByIdAndUserId(id,userId).orElseThrow(()->new NoSuchElementException("Tracker entry not found"));applications.delete(a);}
}
