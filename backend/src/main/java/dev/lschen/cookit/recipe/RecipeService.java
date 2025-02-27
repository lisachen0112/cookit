package dev.lschen.cookit.recipe;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.ingredient.IngredientRepository;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRepository;
import dev.lschen.cookit.instruction.InstructionRequest;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        List<Ingredient> ingredients = request.ingredients().stream()
                        .map(ingredient -> Ingredient.builder()
                                .content(ingredient)
                                .recipe(recipe)
                                .build()
                        ).toList();
        recipe.setIngredients(ingredients);

        if (request.instructions() != null && !request.instructions().isEmpty()) {
            List<Instruction> instructions = request.instructions().stream()
                    .map(inst -> Instruction.builder()
                            .orderIndex(inst.orderIndex())
                            .type(inst.type())
                            .content(inst.content())
                            .recipe(recipe)
                            .build()
                    ).toList();
            recipe.setInstructions(instructions);
        }

        recipeRepository.save(recipe);

        return recipeMapper.toRecipeResponse(recipe);
    }

    public PageResponse<RecipeListResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending());
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        List<RecipeListResponse> response = recipes.stream()
                .map(recipeMapper::toRecipeListResponse)
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
        if (!Objects.equals(user.getUserId(), recipe.getCreatedBy().getUserId())) {
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
        if (!new HashSet<>(recipe.getIngredients().stream().map(Ingredient::getContent).toList())
                .equals(new HashSet<>(request.ingredients()))) {
            updateIngredients(recipe, request.ingredients());
        }

        if (!areInstructionsEqual(recipe.getInstructions(), request.instructions())) {
            updateInstructions(recipe, request.instructions());
        }
        recipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(recipe);
    }

    private boolean areInstructionsEqual(List<Instruction> existing, List<InstructionRequest> newInstructions) {
        if (existing.size() != newInstructions.size()) {
            return false;
        }

        Map<Integer, Instruction> existingMap = existing.stream()
                .collect(Collectors.toMap(Instruction::getOrderIndex, i -> i));

        Map<Integer, InstructionRequest> newMap = newInstructions.stream()
                .collect(Collectors.toMap(InstructionRequest::orderIndex, i -> i));

        return existingMap.entrySet().stream().allMatch(entry -> {
            Instruction existingInstruction = entry.getValue();
            InstructionRequest newInstruction = newMap.get(entry.getKey());
            return newInstruction != null
                    && Objects.equals(existingInstruction.getType(), newInstruction.type())
                    && Objects.equals(existingInstruction.getContent(), newInstruction.content());
        });
    }

    private void updateInstructions(Recipe recipe, List<InstructionRequest> instructionsRequest) {
        recipe.getInstructions().clear();
        instructionRepository.deleteByRecipe(recipe);
        instructionRepository.flush();

        List<Instruction> updatedInstructions = instructionsRequest.stream()
                .map(inst -> Instruction.builder()
                        .orderIndex(inst.orderIndex())
                        .type(inst.type())
                        .recipe(recipe)
                        .content(inst.content())
                        .build()
                ).toList();
        recipe.getInstructions().addAll(updatedInstructions);
    }

    private void updateIngredients(Recipe recipe, List<String> ingredientsRequest) {
        recipe.getIngredients().clear();

        List<Ingredient> updatedIngredients = ingredientsRequest.stream()
                .map(ingredient -> Ingredient.builder()
                        .content(ingredient)
                        .recipe(recipe)
                        .build())
                .toList();
        recipe.getIngredients().addAll(updatedIngredients);
    }

    public PageResponse<RecipeResponse> findRecipesByUserId(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModifiedDate").descending());
        Page<Recipe> recipes = recipeRepository.findByCreatedBy_UserId(pageable, userId);

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
