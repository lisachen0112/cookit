package dev.lschen.cookit.comment;

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

    public Comment addComment(CommentRequest request, Long recipeId) {
        Recipe recipe = recipeService.findById(recipeId);
        Comment comment = commentMapper.toComment(request, recipe);

        return commentRepository.save(comment);
    }

    public CommentResponse findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return commentMapper.toCommentResponse(comment);
    }

    public Comment patchById(CommentRequest request, Long commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUsername(), comment.getCommentedBy().getUsername())) {
            throw new RuntimeException("Cannot update other users comment");
        }
        comment.setContent(request.content());
        return commentRepository.save(comment);
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
        Recipe recipe = recipeService.findById(recipeId);
        List<Comment> comments = commentRepository.findByRecipe(recipe);
        return comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }
}
