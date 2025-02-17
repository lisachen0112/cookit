package dev.lschen.cookit.authentication;

import dev.lschen.cookit.activation.ActivationTokenService;
import dev.lschen.cookit.email.EmailService;
import dev.lschen.cookit.security.JwtService;
import dev.lschen.cookit.user.User;
import dev.lschen.cookit.user.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ActivationTokenService activationTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .username("testUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .accountLocked(false)
                .enabled(false)
                .build();
    }

    @Test
    public void RegistrationTriggersUserSavingTokenGenerationEmailSending() throws MessagingException {
        RegistrationRequest request = RegistrationRequest.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email@email.com")
                .password("password")
                .username("username")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(activationTokenService.generateToken(any(User.class))).thenReturn("activation-code");

        authenticationService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(activationTokenService, times(1)).generateToken(any(User.class));
        verify(emailService, times(1)).sendActivationEmail(any(User.class), eq("activation-code"));
    }


    @Test
    public void ActivatingAccountTriggersActivationTokenVerification() throws MessagingException {
        authenticationService.activateAccount("some-token");

        verify(activationTokenService, times(1)).verifyToken(anyString());
    }

    @Test
    public void AuthenticatingTriggersJwtGenerationAndPasswordVerification()  {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("username")
                .password("password")
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(mockUser);

        String jwtToken = "mock-jwt-token";
        when(jwtService.generateToken(any(), eq(mockUser))).thenReturn(jwtToken);

        AuthenticationResponse response = authenticationService.authenticate(request);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(any(), eq(mockUser));
        assertThat(response).isNotNull();
        assertThat(jwtToken).isEqualTo(response.getToken());
    }
}