package dev.lschen.cookit.authentication;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AuthenticationResponse {
    private final String token;
}
