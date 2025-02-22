package dev.lschen.cookit.recipe;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRepository;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final InstructionRepository instructionRepository;
    private final RecipeMapper recipeMapper;

    public Recipe findRecipeOrThrowException(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
    }

    public RecipeResponse createRecipe(RecipeRequest request) {
        Recipe recipe = recipeMapper.toRecipe(request);
        recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
        recipe.getInstructions().forEach(instruction -> instruction.setRecipe(recipe));
        recipeRepository.save(recipe);

        return recipeMapper.toRecipeResponse(recipe);
    }

    public PageResponse<RecipeResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending());
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        List<RecipeResponse> response = recipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .toList();

        return new PageResponse<>(
                response,
                recipes.getNumber(),
                recipes.getSize(),
                recipes.getTotalElements(),
                recipes.getTotalPages(),
                recipes.isFirst(),
                recipes.isLast()
        );
    }

    public RecipeResponse findById(Long id) {
        Recipe recipe = findRecipeOrThrowException(id);
        return recipeMapper.toRecipeResponse(recipe);
    }

    public void deleteById(Long id) {
        findRecipeOrThrowException(id);
        recipeRepository.deleteById(id);
    }

    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest request, Authentication authentication) {
        Recipe recipe = findRecipeOrThrowException(id);

        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUsername(), recipe.getCreatedBy().getUsername())) {
            throw new OperationNotPermittedException("Cannot modify other users recipes");
        }

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
        recipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(recipe);
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

    public PageResponse<RecipeResponse> findRecipesByUser(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending());
        Page<Recipe> recipes = recipeRepository.findByCreatedBy_Username(pageable, username);

        List<RecipeResponse> response = recipes.stream()
                .map(recipeMapper::toRecipeResponse)
                .toList();

        return new PageResponse<>(
                response,
                recipes.getNumber(),
                recipes.getSize(),
                recipes.getTotalElements(),
                recipes.getTotalPages(),
                recipes.isFirst(),
                recipes.isLast()
        );
    }
}
