package in.schemesathi.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock AppUserRepository repo;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwt;
    @InjectMocks AuthService service;

    @Test void registerRejectsExistingEmail() {
        when(repo.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.register(new AuthDtos.RegisterRequest("Test User", "test@example.com", "password1234")));
    }

    @Test void registerCreatesNewUser() {
        when(repo.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(encoder.encode("password1234")).thenReturn("hashed");
        AppUser saved = mock(AppUser.class);
        when(saved.getId()).thenReturn(UUID.randomUUID());
        when(saved.getEmail()).thenReturn("new@example.com");
        when(saved.getFullName()).thenReturn("New User");
        when(saved.getRole()).thenReturn("CITIZEN");
        when(repo.save(any(AppUser.class))).thenReturn(saved);
        when(jwt.issue(saved)).thenReturn("jwt-token");
        var response = service.register(new AuthDtos.RegisterRequest("New User", "new@example.com", "password1234"));
        assertEquals("jwt-token", response.token());
        assertEquals("New User", response.fullName());
    }

    @Test void loginRejectsInvalidEmail() {
        when(repo.findByEmailIgnoreCase("bad@example.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.login(new AuthDtos.LoginRequest("bad@example.com", "password")));
    }

    @Test void loginRejectsWrongPassword() {
        AppUser user = mock(AppUser.class);
        when(user.getPasswordHash()).thenReturn("hashed");
        when(repo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.login(new AuthDtos.LoginRequest("test@example.com", "wrong")));
    }

    @Test void loginSucceedsWithCorrectCredentials() {
        AppUser user = mock(AppUser.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getPasswordHash()).thenReturn("hashed");
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getFullName()).thenReturn("Test User");
        when(user.getRole()).thenReturn("CITIZEN");
        when(repo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("correct", "hashed")).thenReturn(true);
        when(jwt.issue(user)).thenReturn("jwt-token");
        var response = service.login(new AuthDtos.LoginRequest("test@example.com", "correct"));
        assertEquals("jwt-token", response.token());
    }
}
