package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public Long createRecipe(RecipeRequest request) {
        List<Ingredient> ingredients = request.ingredients().stream()
                .map(ingredientRequest -> Ingredient.builder()
                        .name(ingredientRequest.getName())
                        .quantity(ingredientRequest.getQuantity())
                        .measurement(ingredientRequest.getMeasurement())
                        .build())
                .toList();

        Recipe recipe = Recipe.builder()
                .title(request.title())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .videoUrl(request.videoUrl())
                .ingredients(ingredients)
                .build();

        ingredients.forEach(ingredient -> ingredient.setRecipe(recipe));

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
}
