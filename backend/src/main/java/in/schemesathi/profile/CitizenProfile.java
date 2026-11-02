package in.schemesathi.profile;

import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
import java.time.LocalDate; import java.time.Instant; import java.util.Set; import java.util.UUID;

@Entity @Table(name="citizen_profiles")
public class CitizenProfile {
 @Id @Column(name="user_id") private UUID userId;
 @Column(name="date_of_birth") private LocalDate dateOfBirth;
 @Column(name="state_code",length=2) private String stateCode;
 @Column(name="district",length=100) private String district;
 @Column(name="gender",length=30) private String gender;
 @Column(name="marital_status",length=30) private String maritalStatus;
 @Column(name="annual_income") private Long annualIncome;
 @Column(name="employment_status",length=40) private String employmentStatus;
 @Column(name="education_level",length=60) private String educationLevel;
 @Column(name="social_category",length=30) private String socialCategory;
 @Column(name="residence_type",length=20) private String residenceType;
 @Column(name="disability") private Boolean disability;
 @JdbcTypeCode(SqlTypes.JSON) @Column(name="interests",columnDefinition="jsonb") private Set<String> interests;
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected CitizenProfile(){}
 public UUID getUserId(){return userId;} public LocalDate getDateOfBirth(){return dateOfBirth;} public String getStateCode(){return stateCode;} public String getDistrict(){return district;} public String getGender(){return gender;} public String getMaritalStatus(){return maritalStatus;} public Long getAnnualIncome(){return annualIncome;} public String getEmploymentStatus(){return employmentStatus;} public String getEducationLevel(){return educationLevel;} public String getSocialCategory(){return socialCategory;} public String getResidenceType(){return residenceType;} public Boolean getDisability(){return disability;} public Set<String> getInterests(){return interests;}
 public void update(UUID userId,ProfileDtos.UpsertRequest r){this.userId=userId;dateOfBirth=r.dateOfBirth();stateCode=normalize(r.stateCode());district=r.district();gender=r.gender();maritalStatus=r.maritalStatus();annualIncome=r.annualIncome();employmentStatus=r.employmentStatus();educationLevel=r.educationLevel();socialCategory=r.socialCategory();residenceType=r.residenceType();disability=r.disability();interests=r.interests();updatedAt=Instant.now();}
 private String normalize(String s){return s==null?null:s.trim().toUpperCase();}
}
