package dev.lschen.cookit.recipe;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

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

//    @PatchMapping("/{id}")
//    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe updatedRecipe) {
//        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
//        if (optionalRecipe.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        Recipe existingRecipe = optionalRecipe.get();
//
//        // Update the fields of the existing recipe
//        existingRecipe.setTitle(updatedRecipe.getTitle());
//        existingRecipe.setDescription(updatedRecipe.getDescription());
//        existingRecipe.setImageUrl(updatedRecipe.getImageUrl());
//        existingRecipe.setVideoUrl(updatedRecipe.getVideoUrl());
//        existingRecipe.getIngredients().clear(); // Remove existing ingredients
//        for (Ingredient ingredient : updatedRecipe.getIngredients()) {
//            ingredient.setRecipe(existingRecipe); // Ensure the relationship is set
//        }
//        existingRecipe.getIngredients().addAll(updatedRecipe.getIngredients());
//
//        return ResponseEntity.ok(recipeRepository.save(existingRecipe));
//    }
//

}