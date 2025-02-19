package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final InstructionRepository instructionRepository;

    public Recipe createRecipe(RecipeRequest request) {
        Recipe recipe = Recipe.builder()
                .title(request.title())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .videoUrl(request.videoUrl())
                .ingredients(request.ingredients())
                .instructions(request.instructions())
                .build();

        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
        recipe.getInstructions().forEach(instruction -> instruction.setRecipe(recipe));

        return recipeRepository.save(recipe);
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
    }

    public void deleteById(Long id) {
        findById(id);
        recipeRepository.deleteById(id);
    }

    @Transactional
    public Recipe updateRecipe(Long id, RecipeRequest request) {
        Recipe recipe = findById(id);

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

        if (!recipe.getInstructions().equals(request.instructions())) {
            updateInstructions(recipe, request.instructions());
        }

        return recipeRepository.save(recipe);
    }

    private void updateInstructions(Recipe recipe, List<Instruction> instructions) {
        instructionRepository.deleteByRecipe(recipe);
        instructionRepository.flush();
        recipe.getInstructions().clear();
        instructions.forEach(instruction-> instruction.setRecipe(recipe));
        recipe.getInstructions().addAll(instructions);
    }

    private void updateIngredients(Recipe recipe, List<Ingredient> ingredients) {
        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(ingredients);
        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
    }
}
