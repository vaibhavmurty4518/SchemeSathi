package in.schemesathi.scheme;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchemeReviewServiceTest {

    @Mock SchemeRepository schemes;
    @Mock SchemeReviewRepository reviews;
    @InjectMocks SchemeReviewService service;

    @Test
    void decideThrowsIfSchemeNotFound() {
        UUID schemeId = UUID.randomUUID();
        when(schemes.findById(schemeId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
            () -> service.decide(schemeId, "reviewer1", "good", true));
    }

    @Test
    void decideThrowsIfSchemeNotInReview() {
        UUID schemeId = UUID.randomUUID();
        GovernmentScheme scheme = mock(GovernmentScheme.class);
        when(scheme.getReviewStatus()).thenReturn("DRAFT");
        when(schemes.findById(schemeId)).thenReturn(Optional.of(scheme));
        assertThrows(IllegalStateException.class,
            () -> service.decide(schemeId, "reviewer1", "comment", true));
    }

    @Test
    void approvePublishesScheme() {
        UUID schemeId = UUID.randomUUID();
        GovernmentScheme scheme = mock(GovernmentScheme.class);
        when(scheme.getReviewStatus()).thenReturn("IN_REVIEW");
        when(scheme.isPublished()).thenReturn(true);
        when(scheme.getVerifiedAt()).thenReturn(Instant.now());
        when(schemes.findById(schemeId)).thenReturn(Optional.of(scheme));
        when(schemes.save(scheme)).thenReturn(scheme);
        when(reviews.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = service.decide(schemeId, "admin1", "looks good", true);

        verify(scheme).setReviewStatus("APPROVED");
        verify(scheme).setPublished(true);
        verify(scheme).setVerifiedAt(any());
        assertEquals("admin1", response.reviewerId());
    }

    @Test
    void rejectDoesNotPublish() {
        UUID schemeId = UUID.randomUUID();
        GovernmentScheme scheme = mock(GovernmentScheme.class);
        when(scheme.getReviewStatus()).thenReturn("IN_REVIEW");
        when(scheme.isPublished()).thenReturn(false);
        when(schemes.findById(schemeId)).thenReturn(Optional.of(scheme));
        when(schemes.save(scheme)).thenReturn(scheme);
        when(reviews.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = service.decide(schemeId, "admin1", "needs fixes", false);

        verify(scheme).setReviewStatus("REJECTED");
        verify(scheme).setPublished(false);
        verify(scheme, never()).setVerifiedAt(any());
    }
}
