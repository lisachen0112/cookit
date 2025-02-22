package dev.lschen.cookit.recipe.service;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.recipe.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RecipeServiceRetrievalTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    private Recipe recipe;
    private RecipeResponse response;

    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .recipeId(1L)
                .build();

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .ingredientId(1L)
                .build());
        ingredients.add(Ingredient.builder()
                .ingredientId(2L)
                .build());
        recipe.setIngredients(ingredients);

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(Instruction.builder()
                .recipe(recipe)
                .build());
        recipe.setInstructions(instructions);

        response = new RecipeResponse(
                1L,
                "title",
                "description",
                null,
                null,
                ingredients,
                instructions,
                null,
                null,
                null
        );
    }

    @Test
    public void getAllRecipesFromRepository() {
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe), PageRequest.of(0, 10), 1);
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        PageResponse<RecipeResponse> results = recipeService.findAll(0, 10);

        verify(recipeRepository, times(1)).findAll(any(Pageable.class));

        assertThat(results.getContent()).isEqualTo(List.of(response));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);
    }

    @Test
    public void throwExceptionWhenTryingToGetNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void getRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        RecipeResponse result = recipeService.findById(1L);

        verify(recipeRepository, times(1)).findById(1L);
        assertThat(result).isEqualTo(response);
    }

    @Test
    public void getRecipeByUserSuccessfullyTest() {
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe), PageRequest.of(0, 10), 1);
        when(recipeRepository.findByCreatedBy_UserId(any(Pageable.class), anyLong()))
                .thenReturn(recipePage);
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        PageResponse<RecipeResponse> results = recipeService.findRecipesByUserId(0, 10,1L);

        assertThat(results.getContent()).isEqualTo(List.of(response));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);

        verify(recipeRepository, times(1))
                .findByCreatedBy_UserId(any(Pageable.class), anyLong());
    }
}
