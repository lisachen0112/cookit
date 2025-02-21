package dev.lschen.cookit.recipe.service;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.recipe.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RecipeServiceCreationTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;
    private RecipeRequest request;
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

        request = new RecipeRequest("title",
                "description",
                "imageURL",
                "videoUrl",
                ingredients,
                instructions
        );

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
    public void createRecipeSuccessfullyFromRequest() {
        when(recipeMapper.toRecipe(any(RecipeRequest.class))).thenReturn(recipe);
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        RecipeResponse result = recipeService.createRecipe(request);

        assertThat(result.title()).isEqualTo(request.title());
        assertThat(result.description()).isEqualTo(request.description());
        assertThat(result.ingredients().size()).isEqualTo(request.ingredients().size());
        assertThat(result.instructions().size()).isEqualTo(request.instructions().size());

        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}
