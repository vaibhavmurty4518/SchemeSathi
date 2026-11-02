package in.schemesathi.eligibility;

import in.schemesathi.profile.CitizenProfile;
import in.schemesathi.profile.CitizenProfileRepository;
import in.schemesathi.scheme.GovernmentScheme;
import in.schemesathi.scheme.SchemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EligibilityServiceTest {

    @Mock CitizenProfileRepository profiles;
    @Mock SchemeRepository schemes;
    @InjectMocks EligibilityService service;

    @Test void assessThrowsIfProfileMissing() {
        UUID userId = UUID.randomUUID(), schemeId = UUID.randomUUID();
        when(profiles.findById(userId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.assess(userId, schemeId));
    }

    @Test void assessThrowsIfSchemeNotFound() {
        UUID userId = UUID.randomUUID(), schemeId = UUID.randomUUID();
        when(profiles.findById(userId)).thenReturn(Optional.of(mock(CitizenProfile.class)));
        when(schemes.findById(schemeId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.assess(userId, schemeId));
    }

    @Test void assessThrowsIfSchemeNotPublished() {
        UUID userId = UUID.randomUUID(), schemeId = UUID.randomUUID();
        GovernmentScheme scheme = mock(GovernmentScheme.class);
        when(scheme.isPublished()).thenReturn(false);
        when(scheme.getReviewStatus()).thenReturn("DRAFT");
        when(profiles.findById(userId)).thenReturn(Optional.of(mock(CitizenProfile.class)));
        when(schemes.findById(schemeId)).thenReturn(Optional.of(scheme));
        assertThrows(IllegalArgumentException.class, () -> service.assess(userId, schemeId));
    }
}
