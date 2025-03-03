package dev.lschen.cookit.recipe.service;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.ContentType;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRequest;
import dev.lschen.cookit.recipe.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
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

        List<InstructionRequest> instructionsRequest = new ArrayList<>();
        instructionsRequest.add(new InstructionRequest(0, ContentType.TEXT, "STEP1", null));

        request = new RecipeRequest("title",
                "description",
                null,
                "videoUrl",
                List.of("ingredient1", "ingredient2"),
                instructionsRequest
        );

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .ingredientId(1L)
                .content("ingredient1")
                .build());
        ingredients.add(Ingredient.builder()
                .ingredientId(2L)
                .content("ingredient2")
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
    public void createRecipeSuccessfullyFromRequest() throws IOException {
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
