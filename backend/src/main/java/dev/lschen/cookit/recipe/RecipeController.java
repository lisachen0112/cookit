package dev.lschen.cookit.recipe;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping()
    ResponseEntity<Void> createRecipe(@RequestBody @Valid RecipeRequest recipe) {
        Recipe result = recipeService.createRecipe(recipe);
        return ResponseEntity.created(URI.create("/recipes/" + result.getRecipeId())).build();
    }

    @GetMapping
    ResponseEntity<List<Recipe>> getAll() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable Long id, @RequestBody RecipeRequest request) {
        recipeService.updateRecipe(id, request);
        return ResponseEntity.ok().build();
    }
}