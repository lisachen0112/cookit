package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
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
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;

    private RecipeRequest request;

    private List<Ingredient> ingredients;
    private List<Instruction> instructions;

    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .description("description")
                .ingredients(new ArrayList<>())
                .instructions(new ArrayList<>())
                .build();

        ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .ingredientId(1L)
                .name("ingredient1")
                .quantity(10)
                .measurement("grams")
                .recipe(recipe)
                .build());
        ingredients.add(Ingredient.builder()
                .ingredientId(2L)
                .name("ingredient2")
                .quantity(5)
                .measurement("cups")
                .recipe(recipe)
                .build());
        recipe.getIngredients().addAll(ingredients);

        instructions = new ArrayList<>();
        instructions.add(Instruction.builder()
                        .recipe(recipe)
                        .orderIndex(0)
                        .content("Step 1")
                        .type(Instruction.ContentType.TITLE)
                .build());
        recipe.getInstructions().addAll(instructions);

        request = new RecipeRequest("title",
                "description",
                "imageURL",
                "videoUrl",
                ingredients,
                instructions
        );
    }

    @Test
    public void RecipeCreatedCorrectlyFromRequest() {
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(request);
        System.out.println(result);

        assertThat(result.getTitle()).isEqualTo(request.title());
        assertThat(result.getDescription()).isEqualTo(request.description());
        assertThat(result.getIngredients().size()).isEqualTo(ingredients.size());
        assertThat(result.getInstructions().size()).isEqualTo(instructions.size());

        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    public void GetAllRecipesFromRepository() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<Recipe> recipes = recipeService.findAll();

        verify(recipeRepository, times(1)).findAll();

        assertThat(recipes).isEqualTo(List.of(recipe));
    }

    @Test
    public void ThrowExceptionWhenTryingToGetNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void ReturnRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        Recipe result = recipeService.findById(1L);

        verify(recipeRepository, times(1)).findById(1L);

        assertThat(result).isEqualTo(recipe);
    }

    @Test
    public void ThrowExceptionWhenTryingToDeleteNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void DeleteRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        recipeService.deleteById(1L);

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void ThrowExceptionWhenTryingToUpdateNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).save(any(Recipe.class));
    }

    @Test
    public void UpdateRecipeIfExists() {
        List<Ingredient> newIngredients = new ArrayList<>();
        newIngredients.add(Ingredient.builder()
                .ingredientId(1L)
                .name("ingredient1")
                .quantity(10)
                .measurement("grams")
                .build());

        List<Instruction> newInstructions = new ArrayList<>();
        newInstructions.add(Instruction.builder()
                .orderIndex(0)
                .content("Wash veggies")
                .type(Instruction.ContentType.TEXT)
                .build());

        RecipeRequest updateRequest = new RecipeRequest("new title",
                "new description",
                "newImageUrl",
                "newVideoUrl",
                newIngredients,
                newInstructions);

        Recipe expectedRecipe = Recipe.builder()
                .recipeId(1L)
                .title(updateRequest.title())
                .imageUrl(updateRequest.imageUrl())
                .videoUrl(updateRequest.videoUrl())
                .description(updateRequest.description())
                .ingredients(newIngredients)
                .instructions(newInstructions)
                .build();
        newIngredients.forEach(ingredient -> ingredient.setRecipe(expectedRecipe));
        newInstructions.forEach(instruction -> instruction.setRecipe(expectedRecipe));


        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe newRecipe = recipeService.updateRecipe(1L, updateRequest);

        assertThat(newRecipe.getTitle()).isEqualTo(expectedRecipe.getTitle());
        assertThat(newRecipe.getDescription()).isEqualTo(expectedRecipe.getDescription());
        assertThat(newRecipe.getImageUrl()).isEqualTo(expectedRecipe.getImageUrl());
        assertThat(newRecipe.getVideoUrl()).isEqualTo(expectedRecipe.getVideoUrl());
        assertThat(newRecipe.getIngredients()).isEqualTo(expectedRecipe.getIngredients());
        assertThat(newRecipe.getInstructions()).isEqualTo(expectedRecipe.getInstructions());

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}