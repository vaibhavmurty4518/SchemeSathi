package in.schemesathi.scheme;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SchemeService {
  private final SchemeRepository repo;

  public SchemeService(SchemeRepository repo) {
    this.repo = repo;
  }

  public List<SchemeDtos.SchemeResponse> list(String state, String category) {
    String st = (state == null || state.trim().isEmpty() || "ALL".equalsIgnoreCase(state.trim()))
        ? null
        : state.trim().toUpperCase();
    String cat = (category == null || category.trim().isEmpty())
        ? null
        : category.trim().toLowerCase();

    return repo.findAll().stream()
        .filter(GovernmentScheme::isPublished)
        .filter(s -> "APPROVED".equals(s.getReviewStatus()))
        .filter(s -> {
          // If no state is specified ("All States / Central"), show all published schemes
          if (st == null) {
            return true;
          }
          // When a specific state is chosen, always include Central schemes
          if ("CENTRAL".equalsIgnoreCase(s.getScope())) {
            return true;
          }
          // Only include state-specific schemes that match the requested state
          return s.getStateCode() != null && st.equalsIgnoreCase(s.getStateCode().trim());
        })
        .filter(s -> {
          if (cat == null) {
            return true;
          }
          boolean matchCategory = s.getCategory() != null && s.getCategory().toLowerCase().contains(cat);
          boolean matchTitle = s.getTitle() != null && s.getTitle().toLowerCase().contains(cat);
          return matchCategory || matchTitle;
        })
        .sorted(Comparator.comparing(GovernmentScheme::getScope).reversed()
            .thenComparing(GovernmentScheme::getTitle))
        .map(SchemeDtos.SchemeResponse::new)
        .toList();
  }

  public SchemeDtos.SchemeResponse detail(UUID id) {
    GovernmentScheme s = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Scheme not found"));
    if (!s.isPublished() || !"APPROVED".equals(s.getReviewStatus())) {
      throw new IllegalArgumentException("Scheme is not publicly available");
    }
    return new SchemeDtos.SchemeResponse(s);
  }
}
