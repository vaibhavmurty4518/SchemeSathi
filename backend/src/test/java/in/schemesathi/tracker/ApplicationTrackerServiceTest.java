package in.schemesathi.tracker;

import in.schemesathi.scheme.SchemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationTrackerServiceTest {
  private CitizenSchemeApplicationRepository applications;
  private SchemeRepository schemes;
  private ApplicationTrackerService service;
  private UUID userId, schemeId;
  @BeforeEach void setUp(){applications=mock(CitizenSchemeApplicationRepository.class);schemes=mock(SchemeRepository.class);service=new ApplicationTrackerService(applications,schemes);userId=UUID.randomUUID();schemeId=UUID.randomUUID();}
  @Test void rejectsUnknownScheme(){when(schemes.existsById(schemeId)).thenReturn(false);assertThrows(java.util.NoSuchElementException.class,()->service.create(userId,new ApplicationTrackerDtos.CreateRequest(schemeId,"")));verify(applications,never()).save(any());}
  @Test void rejectsDuplicateTrackedScheme(){when(schemes.existsById(schemeId)).thenReturn(true);when(applications.existsByUserIdAndSchemeId(userId,schemeId)).thenReturn(true);assertThrows(IllegalArgumentException.class,()->service.create(userId,new ApplicationTrackerDtos.CreateRequest(schemeId,"")));verify(applications,never()).save(any());}
  @Test void createsTrackerEntryForKnownScheme(){when(schemes.existsById(schemeId)).thenReturn(true);when(applications.existsByUserIdAndSchemeId(userId,schemeId)).thenReturn(false);when(applications.save(any())).thenAnswer(invocation->invocation.getArgument(0));var response=service.create(userId,new ApplicationTrackerDtos.CreateRequest(schemeId,"Keep income certificate ready"));assertEquals(schemeId,response.schemeId());assertEquals("INTERESTED",response.status());}
}
