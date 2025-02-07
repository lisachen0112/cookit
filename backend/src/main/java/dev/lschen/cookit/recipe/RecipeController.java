package dev.lschen.cookit.recipe;

import org.apache.coyote.RequestInfo;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping
    ResponseEntity<List<Recipe>> getAll() {
        return new ResponseEntity<>(recipeRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/search")
    ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String keyword) {
        return new ResponseEntity<>(recipeRepository.searchByTitleOrDescription(keyword), HttpStatus.OK);
    }

    @PostMapping()
    ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        return new ResponseEntity<>(recipeRepository.save(recipe), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe updatedRecipe) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (optionalRecipe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Recipe existingRecipe = optionalRecipe.get();

        // Update the fields of the existing recipe
        existingRecipe.setTitle(updatedRecipe.getTitle());
        existingRecipe.setDescription(updatedRecipe.getDescription());
        existingRecipe.setImageUrl(updatedRecipe.getImageUrl());
        existingRecipe.setVideoUrl(updatedRecipe.getVideoUrl());
        existingRecipe.getIngredients().clear(); // Remove existing ingredients
        for (Ingredient ingredient : updatedRecipe.getIngredients()) {
            ingredient.setRecipe(existingRecipe); // Ensure the relationship is set
        }
        existingRecipe.getIngredients().addAll(updatedRecipe.getIngredients());

        return ResponseEntity.ok(recipeRepository.save(existingRecipe));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deleteRecipe(@PathVariable Long id) {
        recipeRepository.deleteById(id);
    }

}
