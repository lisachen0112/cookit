package dev.lschen.cookit.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CommentRequest (
        @NotNull(message = "Comment content cannot be empty")
        @NotEmpty(message = "Comment content cannot be empty")
        String content
){
}
