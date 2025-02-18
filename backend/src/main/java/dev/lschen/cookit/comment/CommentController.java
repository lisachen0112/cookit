package dev.lschen.cookit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("recipes/comments")
@RequiredArgsConstructor
public class CommentController {

    @GetMapping("/{recipe-id}")
    ResponseEntity<Void> getCommentForRecipe(@PathVariable("recipe-id") Long recipeId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{recipe-id}")
    ResponseEntity<Void> postCommentForRecipe(@PathVariable("recipe-id") Long recipeId) {
        URI location = null;
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{recipe-id}")
    ResponseEntity<Void> patchCommentForRecipe(@PathVariable("recipe-id") Long recipeId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recipe-id}")
    ResponseEntity<Void> deleteCommentForRecipe(@PathVariable("recipe-id") Long recipeId) {
        return ResponseEntity.noContent().build();
    }
}
