package dev.lschen.cookit.comment;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        if (!Objects.equals(user.getUserId(), comment.getCommentedBy().getUserId())) {
            throw new OperationNotPermittedException("Cannot update other users comment");
        }
        comment.setContent(request.content());
        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public void deleteById(Long commentId, Authentication authentication) {
        Comment comment = findCommentOrThrowException(commentId);

        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUserId(), comment.getCommentedBy().getUserId())) {
            throw new OperationNotPermittedException("Cannot delete other users comment");
        }
        commentRepository.deleteById(commentId);
    }

    public PageResponse<CommentResponse> getAllCommentsForRecipe(Long recipeId, int page, int size) {
         recipeService.findRecipeOrThrowException(recipeId);
         Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending());
         Page<Comment> comments = commentRepository.findByRecipe_RecipeId(pageable, recipeId);
         List<CommentResponse> response = comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();

         return new PageResponse<>(
                 response,
                 comments.getNumber(),
                 comments.getSize(),
                 comments.getTotalElements(),
                 comments.getTotalPages(),
                 comments.isFirst(),
                 comments.isLast()
         );
    }
}
