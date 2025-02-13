package dev.lschen.cookit.authentication;

import dev.lschen.cookit.activation.ActivationTokenService;
import dev.lschen.cookit.email.EmailService;
import dev.lschen.cookit.user.User;
import dev.lschen.cookit.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;

    public void register(@Valid RegistrationRequest request) throws MessagingException {
        var user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .build();
        userRepository.save(user);

        var activationCode = activationTokenService.generateToken(user);
        emailService.sendActivationEmail(user, activationCode);
    }

    public void activateAccount(String token) throws MessagingException {
        activationTokenService.verifyToken(token);
    }
}
