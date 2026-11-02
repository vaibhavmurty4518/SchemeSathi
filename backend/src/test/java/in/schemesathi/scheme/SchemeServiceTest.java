package in.schemesathi.scheme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchemeServiceTest {

    @Mock
    private SchemeRepository repo;

    @InjectMocks
    private SchemeService service;

    private GovernmentScheme centralScheme1;
    private GovernmentScheme centralScheme2;
    private GovernmentScheme mpScheme1;
    private GovernmentScheme mpScheme2;
    private GovernmentScheme unapprovedScheme;

    @BeforeEach
    void setUp() {
        centralScheme1 = mockScheme("PM Kisan", "CENTRAL", null, "Agriculture", true, "APPROVED");
        centralScheme2 = mockScheme("Ayushman Bharat", "CENTRAL", null, "Health", true, "APPROVED");
        mpScheme1 = mockScheme("Ladli Behna", "STATE", "MP", "Women", true, "APPROVED");
        mpScheme2 = mockScheme("Kisan Kalyan MP", "STATE", "MP", "Agriculture", true, "APPROVED");
        unapprovedScheme = mockScheme("Draft Scheme", "CENTRAL", null, "General", true, "DRAFT");

        when(repo.findAll()).thenReturn(List.of(
            centralScheme1,
            centralScheme2,
            mpScheme1,
            mpScheme2,
            unapprovedScheme
        ));
    }

    private GovernmentScheme mockScheme(String title, String scope, String stateCode, String category, boolean published, String status) {
        GovernmentScheme s = mock(GovernmentScheme.class);
        when(s.getId()).thenReturn(UUID.randomUUID());
        when(s.getTitle()).thenReturn(title);
        when(s.getScope()).thenReturn(scope);
        when(s.getStateCode()).thenReturn(stateCode);
        when(s.getCategory()).thenReturn(category);
        when(s.isPublished()).thenReturn(published);
        when(s.getReviewStatus()).thenReturn(status);
        when(s.getSummary()).thenReturn(title + " summary");
        return s;
    }

    @Test
    void allStatesOrNullShowsCentralAndAllStateSchemes() {
        // When state is null or empty, both central and all verified state schemes should appear
        var listNull = service.list(null, null);
        assertEquals(4, listNull.size()); // 2 central + 2 MP approved schemes

        var listEmpty = service.list("", null);
        assertEquals(4, listEmpty.size());

        var listAll = service.list("ALL", null);
        assertEquals(4, listAll.size());
    }

    @Test
    void centralSchemesRemainVisibleWhenStateIsSelected() {
        // For state=MP, central schemes must still be present
        var listMp = service.list("MP", null);
        assertTrue(listMp.stream().anyMatch(s -> "PM Kisan".equals(s.title())));
        assertTrue(listMp.stream().anyMatch(s -> "Ayushman Bharat".equals(s.title())));
        assertTrue(listMp.stream().anyMatch(s -> "Ladli Behna".equals(s.title())));
        assertTrue(listMp.stream().anyMatch(s -> "Kisan Kalyan MP".equals(s.title())));
        assertEquals(4, listMp.size());
    }

    @Test
    void stateSpecificSchemesMustAppearOnlyForMatchingState() {
        // For state=UP, Central schemes must appear, but MP schemes must NOT appear
        var listUp = service.list("UP", null);
        assertEquals(2, listUp.size());
        assertTrue(listUp.stream().allMatch(s -> "CENTRAL".equals(s.scope())));
        assertFalse(listUp.stream().anyMatch(s -> "MP".equals(s.stateCode())));
    }

    @Test
    void selectingAnotherStateShowsOnlyCentralSchemesWhenNoStateRecordsExist() {
        // For state=DL (Delhi), should only return Central schemes, no MP schemes
        var listDl = service.list("DL", null);
        assertEquals(2, listDl.size());
        assertTrue(listDl.stream().anyMatch(s -> "PM Kisan".equals(s.title())));
        assertTrue(listDl.stream().anyMatch(s -> "Ayushman Bharat".equals(s.title())));
        assertFalse(listDl.stream().anyMatch(s -> "STATE".equals(s.scope())));
    }

    @Test
    void categoryFilterNarrowsResultsWithinSelectedState() {
        // For state=MP and category=Agriculture, should match PM Kisan (Central) and Kisan Kalyan (MP)
        var filtered = service.list("MP", "Agriculture");
        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().anyMatch(s -> "PM Kisan".equals(s.title())));
        assertTrue(filtered.stream().anyMatch(s -> "Kisan Kalyan MP".equals(s.title())));
        assertFalse(filtered.stream().anyMatch(s -> "Ayushman Bharat".equals(s.title())));
    }

    @Test
    void unapprovedOrUnpublishedSchemesAreNeverReturned() {
        var list = service.list(null, null);
        assertFalse(list.stream().anyMatch(s -> "Draft Scheme".equals(s.title())));
    }
}
