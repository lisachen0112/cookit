package dev.lschen.cookit.comment;

import java.time.LocalDateTime;

public record CommentResponse (
        Long commentId,
        String content,
        LocalDateTime lastModifiedDate,
        String userId,
        Long recipeId
) {
}
