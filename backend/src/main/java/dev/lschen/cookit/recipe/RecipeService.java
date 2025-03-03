package dev.lschen.cookit.recipe;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.ContentType;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRepository;
import dev.lschen.cookit.instruction.InstructionRequest;
import dev.lschen.cookit.media.MediaCategory;
import dev.lschen.cookit.media.MediaService;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final InstructionRepository instructionRepository;
    private final RecipeMapper recipeMapper;
    private final MediaService mediaService;

    public Recipe findRecipeOrThrowException(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
    }

    public RecipeResponse createRecipe(RecipeRequest request) throws IOException {
        String coverImageUrl = null;
        if (request.coverImage() != null && !request.coverImage().isEmpty()) {
            coverImageUrl = mediaService.uploadMedia(request.coverImage(), MediaCategory.COVER);
        }

        Recipe recipe = recipeMapper.toRecipe(request);
        recipe.setImageUrl(coverImageUrl);

        List<Ingredient> ingredients = request.ingredients().stream()
                        .map(ingredient -> Ingredient.builder()
                                .content(ingredient)
                                .recipe(recipe)
                                .build()
                        ).toList();
        recipe.setIngredients(ingredients);

        if (request.instructions() != null && !request.instructions().isEmpty()) {
            List<Instruction> instructions = new ArrayList<>();
            for (InstructionRequest inst : request.instructions()) {
                String content = inst.content();

                if (inst.type() == ContentType.IMAGE && inst.media() != null && !inst.media().isEmpty()) {
                    content = mediaService.uploadMedia(inst.media(), MediaCategory.INSTRUCTIONS);
                }

                Instruction instruction = Instruction.builder()
                        .orderIndex(inst.orderIndex())
                        .content(content)
                        .type(inst.type())
                        .recipe(recipe)
                        .build();

                instructions.add(instruction);
            }

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

    public void deleteById(Long id, Authentication authentication) {
        Recipe recipe = findRecipeOrThrowException(id);
        User user = (User) authentication.getPrincipal();
        if (!Objects.equals(user.getUserId(), recipe.getCreatedBy().getUserId())) {
            throw new OperationNotPermittedException("Cannot delete other users recipes");
        }

        List<Instruction> instructions = recipe.getInstructions();
        for (Instruction instruction : instructions) {
            if ((instruction.getType() == ContentType.IMAGE) && (instruction.getContent() != null)) {
                String imageFileName = instruction.getContent();
                mediaService.deleteMedia(imageFileName, "instructions/");
            }
        }
        recipeRepository.deleteById(id);
    }


    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest request, Authentication authentication) throws IOException {
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

        // TODO determine if the user wants to delete the photo or no changes were made
        if (request.coverImage() != null && !request.coverImage().isEmpty()) {
            recipe.setImageUrl(mediaService.uploadMedia(request.coverImage(), MediaCategory.COVER));
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

    private void updateInstructions(Recipe recipe, List<InstructionRequest> instructionsRequest) throws IOException {
        // delete images that are not needed anymore
        List<String> imagesToKeep = new ArrayList<>();
        for (InstructionRequest instructionRequest : instructionsRequest) {
            if (instructionRequest.type() == ContentType.IMAGE && instructionRequest.content() != null && !instructionRequest.content().isEmpty()) {
                imagesToKeep.add(instructionRequest.content());
            }
        }

        for (Instruction inst : recipe.getInstructions()) {
            if (inst.getType() == ContentType.IMAGE && inst.getContent() != null && !inst.getContent().isEmpty()
                    && !imagesToKeep.contains(inst.getContent())) {
                String imageFileName = inst.getContent();
                mediaService.deleteMedia(imageFileName, "instructions/");
            }
        }

        // remove previous instructions
        recipe.getInstructions().clear();
        instructionRepository.deleteByRecipe(recipe);
        instructionRepository.flush();

        if (instructionsRequest != null && !instructionsRequest.isEmpty()) {
            List<Instruction> updatedInstructions = new ArrayList<>();
            for (InstructionRequest inst : instructionsRequest) {
                String content = inst.content();

                if (inst.type() == ContentType.IMAGE && inst.media() != null && !inst.media().isEmpty()) {
                    content = mediaService.uploadMedia(inst.media(), MediaCategory.INSTRUCTIONS);
                }

                Instruction instruction = Instruction.builder()
                        .orderIndex(inst.orderIndex())
                        .content(content)
                        .type(inst.type())
                        .recipe(recipe)
                        .build();

                updatedInstructions.add(instruction);
            }

            recipe.getInstructions().addAll(updatedInstructions);
        }
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
