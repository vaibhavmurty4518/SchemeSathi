package in.schemesathi.eligibility;
import java.util.List;import java.util.UUID;
public class EligibilityDtos {public record Assessment(UUID schemeId,String schemeTitle,String status,List<String> matched,List<String> unmet,List<String> missingInformation,String notice){} }
