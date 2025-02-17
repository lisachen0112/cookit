package dev.lschen.cookit.activation;

import dev.lschen.cookit.email.EmailService;
import dev.lschen.cookit.user.User;
import dev.lschen.cookit.user.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ActivationTokenServiceTest {
    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivationTokenService activationTokenService;

    private User user;

    private ActivationToken activationToken;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test-token";

        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("john")
                .password("password")
                .createdDate(LocalDateTime.now())
                .build();

        activationToken = ActivationToken.builder()
                .user(user)
                .token(token)
                .build();
    }

    @Test
    void shouldGenerateTokenWithCorrectLengthAndOnlyDigits() {

        String token = activationTokenService.generateToken(user);

        assertThat(token.matches("^[0-9]{6}$")).isTrue();

        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }

    @Test
    void throwsErrorIfTokenDoesNotExist() {
        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activationTokenService.verifyToken(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token does not exist");

        verify(activationTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    void throwsErrorIfTokenIsExpired() throws MessagingException {
        activationToken.setExpiresAt(LocalDateTime.now().minusMinutes(5));
        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(activationToken));

        assertThatThrownBy(() -> activationTokenService.verifyToken(activationToken.getToken()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token has expired. A new token has been sent");

        verify(activationTokenRepository, times(1)).findByToken(anyString());
        verify(emailService, times(1)).sendActivationEmail(any(User.class), anyString());
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }

    @Test
    void throwsErrorIfUserAssociatedWithTokenDoesNotExist() {
        activationToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(activationToken));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activationTokenService.verifyToken(activationToken.getToken()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User associated with token does not exist");

        verify(activationTokenRepository, times(1)).findByToken(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void activationTokenIsCorrectlyVerified() throws MessagingException {
        activationToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(activationToken));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        activationTokenService.verifyToken(token);

        verify(activationTokenRepository, times(1)).findByToken(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }
}