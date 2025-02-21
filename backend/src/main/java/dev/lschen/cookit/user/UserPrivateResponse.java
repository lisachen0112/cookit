package dev.lschen.cookit.user;

import java.time.LocalDateTime;

public record UserPrivateResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdDate
) implements UserResponse {
}
