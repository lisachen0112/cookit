package dev.lschen.cookit.user;

public record UserPublicResponse(
        Long userId,
        String username
) implements UserResponse {
}
