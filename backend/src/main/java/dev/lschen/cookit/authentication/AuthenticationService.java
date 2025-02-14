package dev.lschen.cookit.authentication;

import dev.lschen.cookit.activation.ActivationTokenService;
import dev.lschen.cookit.email.EmailService;
import dev.lschen.cookit.security.JwtService;
import dev.lschen.cookit.user.User;
import dev.lschen.cookit.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
        );
        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("email", user.getEmail());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

}
