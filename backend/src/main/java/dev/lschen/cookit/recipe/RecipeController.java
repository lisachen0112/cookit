package dev.lschen.cookit.recipe;

import dev.lschen.cookit.favorite.FavoriteRecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final FavoriteRecipeService favoriteService;

    @PostMapping()
    ResponseEntity<Long> createRecipe(@RequestBody @Valid RecipeRequest recipe) {
        return ResponseEntity.ok(recipeService.createRecipe(recipe));
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
        return ResponseEntity.accepted().build();
    }

//    @GetMapping("/search")
//    ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String keyword) {
//        return new ResponseEntity<>(recipeRepository.searchByTitleOrDescription(keyword), HttpStatus.OK);
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable Long id, @RequestBody RecipeRequest request) {
        recipeService.updateRecipe(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/favorite/{recipe-id}")
    ResponseEntity<Void> favoriteRecipe(
            @PathVariable("recipe-id") Long recipeId,
            Authentication authentication) {
        favoriteService.favoriteRecipe(recipeId, authentication);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/favorite/{recipe-id}")
    ResponseEntity<Void> unfavoriteRecipe(
            @PathVariable("recipe-id") Long recipeId,
            Authentication authentication) {
        favoriteService.unfavoriteRecipe(recipeId, authentication);
        return ResponseEntity.noContent().build();
    }
}