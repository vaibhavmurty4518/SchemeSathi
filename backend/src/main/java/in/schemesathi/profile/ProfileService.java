package in.schemesathi.profile;
import org.springframework.stereotype.Service; import java.util.UUID;
@Service public class ProfileService{private final CitizenProfileRepository repo;public ProfileService(CitizenProfileRepository repo){this.repo=repo;}
 public ProfileDtos.ProfileResponse get(UUID id){return repo.findById(id).map(ProfileDtos.ProfileResponse::new).orElseThrow(()->new IllegalArgumentException("Citizen profile not found"));}
 public ProfileDtos.ProfileResponse save(UUID id,ProfileDtos.UpsertRequest r){CitizenProfile p=repo.findById(id).orElseGet(CitizenProfile::new);p.update(id,r);return new ProfileDtos.ProfileResponse(repo.save(p));}}
