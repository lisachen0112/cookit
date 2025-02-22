package dev.lschen.cookit.comment;

import dev.lschen.cookit.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
@Tag(name="Comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{comment-id}")
    ResponseEntity<CommentResponse> getCommentById(@PathVariable("comment-id") Long commentId) {
        CommentResponse response = commentService.findById(commentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{comment-id}")
    ResponseEntity<Void> deleteCommentById(
            @PathVariable("comment-id") Long commentId,
            Authentication authentication) {
        commentService.deleteById(commentId, authentication);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{comment-id}")
    ResponseEntity<CommentResponse> patchCommentForRecipe(
            @PathVariable("comment-id") Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        CommentResponse response = commentService.patchById(request, commentId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recipe/{recipe-id}")
    ResponseEntity<PageResponse<CommentResponse>> getCommentsForRecipe(
            @PathVariable("recipe-id") Long recipeId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        return ResponseEntity.ok(commentService.getAllCommentsForRecipe(recipeId, page, size));
    }

    @PostMapping("/recipe/{recipe-id}")
    ResponseEntity<Void> postCommentForRecipe(
            @PathVariable("recipe-id") Long recipeId,
            @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addComment(request, recipeId);
        URI location = URI.create("comments/" + response.commentId());
        return ResponseEntity.created(location).build();
    }
}
