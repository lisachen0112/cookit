package dev.lschen.cookit.comment;

import dev.lschen.cookit.recipe.Recipe;
import org.springframework.stereotype.Service;

@Service
public class CommentMapper {

    public Comment toComment(CommentRequest request, Recipe recipe) {
        return Comment.builder()
                .content(request.content())
                .recipe(recipe)
                .build();
    }

    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getLastModifiedDate(),
                comment.getCommentedBy().getUserId(),
                comment.getRecipe().getRecipeId()
        );
    }
}
