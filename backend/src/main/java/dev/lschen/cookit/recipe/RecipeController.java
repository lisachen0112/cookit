package dev.lschen.cookit.recipe;

import dev.lschen.cookit.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("recipes")
@RequiredArgsConstructor
@Tag(name="Recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    ResponseEntity<PageResponse<RecipeResponse>> getAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(recipeService.findAll(page, size));
    }

    @PostMapping()
    ResponseEntity<Void> createRecipe(@RequestBody @Valid RecipeRequest recipe) {
        RecipeResponse response = recipeService.createRecipe(recipe);
        return ResponseEntity.created(URI.create("/recipes/" + response.recipeId())).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, request, authentication));
    }
}