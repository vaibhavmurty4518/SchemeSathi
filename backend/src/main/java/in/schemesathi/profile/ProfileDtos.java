package in.schemesathi.profile;
import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.Set;
public class ProfileDtos {
 public record UpsertRequest(@Past LocalDate dateOfBirth,@Pattern(regexp="^[A-Za-z]{2}$") String stateCode,@Size(max=100) String district,@Size(max=30) String gender,@Size(max=30) String maritalStatus,@PositiveOrZero @Max(100000000) Long annualIncome,@Size(max=40) String employmentStatus,@Size(max=60) String educationLevel,@Size(max=30) String socialCategory,@Size(max=20) String residenceType,Boolean disability,Set<@Size(max=60) String> interests){}
 public record ProfileResponse(String userId,LocalDate dateOfBirth,String stateCode,String district,String gender,String maritalStatus,Long annualIncome,String employmentStatus,String educationLevel,String socialCategory,String residenceType,Boolean disability,Set<String> interests){public ProfileResponse(CitizenProfile p){this(p.getUserId().toString(),p.getDateOfBirth(),p.getStateCode(),p.getDistrict(),p.getGender(),p.getMaritalStatus(),p.getAnnualIncome(),p.getEmploymentStatus(),p.getEducationLevel(),p.getSocialCategory(),p.getResidenceType(),p.getDisability(),p.getInterests());}}
}
