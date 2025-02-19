package dev.lschen.cookit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
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
    ResponseEntity<Void> patchCommentForRecipe(
            @PathVariable("comment-id") Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        commentService.patchById(request, commentId, authentication);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipe/{recipe-id}")
    ResponseEntity<List<CommentResponse>> getCommentsForRecipe(@PathVariable("recipe-id") Long recipeId) {
        List<CommentResponse> comments = commentService.getAllCommentsForRecipe(recipeId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/recipe/{recipe-id}")
    ResponseEntity<Void> postCommentForRecipe(
            @PathVariable("recipe-id") Long recipeId,
            @RequestBody CommentRequest request) {
        Comment comment = commentService.addComment(request, recipeId);
        URI location = URI.create("comments/" + comment.getCommentId());
        return ResponseEntity.created(location).build();
    }
}
