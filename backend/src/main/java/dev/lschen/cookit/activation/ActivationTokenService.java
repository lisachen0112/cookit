package dev.lschen.cookit.activation;

import dev.lschen.cookit.email.EmailService;
import dev.lschen.cookit.user.User;
import dev.lschen.cookit.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivationTokenService {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public String generateToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = ActivationToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        activationTokenRepository.save(token);
        return token.getToken();
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    // TODO - check if user is already verified
    // TODO - implement better error handling for FE
    @Transactional
    public void verifyToken(String token) throws MessagingException {
        ActivationToken savedToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token does not exist"));

        // token expired
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            User user = savedToken.getUser();
            var activationCode = generateToken(user);
            emailService.sendActivationEmail(user, activationCode);
            throw new RuntimeException("Token has expired. A new token has been sent");
        }

        var user = userRepository.findByUsername(savedToken.getUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User associated with token does not exist"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        activationTokenRepository.save(savedToken);
    }
}
