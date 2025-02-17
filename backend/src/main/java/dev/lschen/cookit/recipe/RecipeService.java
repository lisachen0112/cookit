package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public Long createRecipe(RecipeRequest request) {

        Recipe recipe = Recipe.builder()
                .title(request.title())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .videoUrl(request.videoUrl())
                .ingredients(request.ingredients())
                .build();

        request.ingredients().forEach(ingredient -> ingredient.setRecipe(recipe));

        return recipeRepository.save(recipe).getRecipeId();
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
    }

    public void deleteById(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new EntityNotFoundException("Recipe not found");
        }
        recipeRepository.deleteById(id);
    }

    public Recipe updateRecipe(Long id, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        if (!Objects.equals(recipe.getTitle(), request.title())) {
            recipe.setTitle(request.title());
        }
        if (!Objects.equals(recipe.getDescription(), request.description())) {
            recipe.setDescription(request.description());
        }
        if (!Objects.equals(recipe.getImageUrl(), request.imageUrl())) {
            recipe.setImageUrl(request.imageUrl());
        }
        if (!Objects.equals(recipe.getVideoUrl(), request.videoUrl())) {
            recipe.setVideoUrl(request.videoUrl());
        }
        if (!recipe.getIngredients().equals(request.ingredients())) {
            updateIngredients(recipe, request.ingredients());
        }

        return recipeRepository.save(recipe);
    }

    private void updateIngredients(Recipe recipe, List<Ingredient> ingredients) {
        recipe.getIngredients().clear();
        ingredients.forEach(ingredient -> ingredient.setRecipe(recipe));
        recipe.getIngredients().addAll(ingredients);
    }
}
