package dev.lschen.cookit.recipe;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    ResponseEntity<List<RecipeResponse>> getAll() {
        return ResponseEntity.ok(recipeService.findAll());
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