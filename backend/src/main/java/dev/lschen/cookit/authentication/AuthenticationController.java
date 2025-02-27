package dev.lschen.cookit.authentication;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name="Authentication")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        service.register(request);
        return ResponseEntity.created(URI.create("")).build();
    }

    @PostMapping("/activate-account")
    public ResponseEntity<?> activateAccount(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}

