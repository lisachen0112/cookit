package dev.lschen.cookit.comment;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeService recipeService;
    private final CommentMapper commentMapper;

    private Comment findCommentOrThrowException(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    public CommentResponse findById(Long commentId) {
        Comment comment = findCommentOrThrowException(commentId);
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse addComment(CommentRequest request, Long recipeId) {
        Recipe recipe = recipeService.findRecipeOrThrowException(recipeId);

        Comment comment = commentMapper.toComment(request, recipe);
        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse patchById(CommentRequest request, Long commentId, Authentication authentication) {
        Comment comment = findCommentOrThrowException(commentId);

        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUsername(), comment.getCommentedBy().getUsername())) {
            throw new OperationNotPermittedException("Cannot update other users comment");
        }
        comment.setContent(request.content());
        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public void deleteById(Long commentId, Authentication authentication) {
        Comment comment = findCommentOrThrowException(commentId);

        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUsername(), comment.getCommentedBy().getUsername())) {
            throw new OperationNotPermittedException("Cannot delete other users comment");
        }
        commentRepository.deleteById(commentId);
    }

    public List<CommentResponse> getAllCommentsForRecipe(Long recipeId) {
        Recipe recipe = recipeService.findRecipeOrThrowException(recipeId);
        List<Comment> comments = recipe.getComments();
        return comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }
}
