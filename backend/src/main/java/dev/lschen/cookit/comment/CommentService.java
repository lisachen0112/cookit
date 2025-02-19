package dev.lschen.cookit.comment;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeRepository;
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
    private final RecipeRepository recipeRepository;
    private final CommentMapper commentMapper;

    public CommentResponse addComment(CommentRequest request, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        Comment comment = commentMapper.toComment(request, recipe);
        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse patchById(CommentRequest request, Long commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUsername(), comment.getCommentedBy().getUsername())) {
            throw new RuntimeException("Cannot update other users comment");
        }
        comment.setContent(request.content());
        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public void deleteById(Long commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUsername(), comment.getCommentedBy().getUsername())) {
            throw new RuntimeException("Cannot delete other users comment");
        }
        commentRepository.deleteById(commentId);
    }

    public List<CommentResponse> getAllCommentsForRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
        List<Comment> comments = recipe.getComments();
        return comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }
}
