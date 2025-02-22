package dev.lschen.cookit.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserPublicResponse toUserPublicResponse(User user) {
        return new UserPublicResponse(user.getUserId(), user.getUsername());
    }

    public UserPrivateResponse toUserPrivateResponse(User user) {
        return new UserPrivateResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedDate()
                );
    }
}
