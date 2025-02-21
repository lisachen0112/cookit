package dev.lschen.cookit.recipe.service;

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
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        List<RecipeResponse> results = recipeService.findAll();

        verify(recipeRepository, times(1)).findAll();
        assertThat(results).isEqualTo(List.of(response));
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
        when(recipeRepository.findByCreatedBy_Username(anyString()))
                .thenReturn(List.of(recipe));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        List<RecipeResponse> responseList = recipeService.findRecipesByUser("test");

        assertThat(responseList).isEqualTo(List.of(response));
        verify(recipeRepository, times(1)).findByCreatedBy_Username("test");
    }
}
