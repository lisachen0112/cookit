package dev.lschen.cookit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{comment-id}")
    ResponseEntity<Comment> getCommentById(@PathVariable("comment-id") Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{comment-id}")
    ResponseEntity<Void> deleteCommentById(@PathVariable("comment-id") Long commentId) {
        commentService.deleteCommentById(commentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{comment-id}")
    ResponseEntity<Void> patchCommentForRecipe(
            @PathVariable("comment-id") Long commentId,
            @RequestBody CommentRequest request) {
        commentService.patchCommentById(request, commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipe/{recipe-id}")
    ResponseEntity<List<Comment>> getCommentsForRecipe(@PathVariable("recipe-id") Long recipeId) {
        List<Comment> comments = commentService.getAllCommentsForRecipe(recipeId);
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
