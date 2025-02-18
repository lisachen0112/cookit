package dev.lschen.cookit.comment;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeService recipeService;

    public Comment addComment(CommentRequest request, Long recipeId) {
        Recipe recipe = recipeService.findById(recipeId);
        Comment comment = Comment.builder()
                .content(request.content())
                .recipe(recipe)
                .build();

        return commentRepository.save(comment);
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    public Comment patchById(CommentRequest request, Long commentId) {
        Comment comment = findById(commentId);
        comment.setContent(request.content());
        return commentRepository.save(comment);
    }

    public void deleteById(Long commentId) {
        findById(commentId);
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getAllCommentsForRecipe(Long recipeId) {
        Recipe recipe = recipeService.findById(recipeId);

        return commentRepository.findByRecipe(recipe);
    }
}
