package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
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

    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .description("description")
                .createdBy(null)
                .createdDate(null)
                .lastModifiedDate(null)
                .ingredients(new ArrayList<>())
                .imageUrl("imageURL")
                .videoUrl("videoUrl")
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


        request = new RecipeRequest("title",
                "description",
                "imageURL",
                "videoUrl",
                ingredients
        );
    }

    @Test
    public void RecipeCreatedCorrectlyFromRequest() {

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Long recipeId = recipeService.createRecipe(request);

        verify(recipeRepository, times(1)).save(any(Recipe.class));

        assertThat(recipeId).isEqualTo(1L);
    }

    @Test
    public void GetAllRecipesFromRepository() {

        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<Recipe> recipes = recipeService.findAll();

        verify(recipeRepository, times(1)).findAll();

        assertThat(recipes).isEqualTo(List.of(recipe));
    }

    @Test
    public void ReturnRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        Recipe result = recipeService.findById(1L);

        verify(recipeRepository, times(1)).findById(1L);

        assertThat(result).isEqualTo(recipe);
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
    public void DeleteRecipeIfExists() {
        when(recipeRepository.existsById(anyLong())).thenReturn(true);

        recipeService.deleteById(1L);

        verify(recipeRepository, times(1)).existsById(1L);
        verify(recipeRepository, times(1)).deleteById(1L);

    }

    @Test
    public void ThrowExceptionWhenTryingToDeleteNonexistentRecipe() {
        when(recipeRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> recipeService.deleteById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).existsById(1L);
    }

    @Test
    public void ThrowExceptionWhenTryingToUpdateNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
    }

    @Test
    public void UpdateRecipeIfExists() {

        RecipeRequest updateRequest = new RecipeRequest("new title",
                "new description",
                "newImageUrl",
                "newVideoUrl",
                ingredients);

        Recipe expectedRecipe = Recipe.builder()
                .recipeId(1L)
                .title(updateRequest.title())
                .description(updateRequest.description())
                .createdBy(null)
                .createdDate(null)
                .lastModifiedDate(null)
                .ingredients(ingredients)
                .imageUrl(null)
                .videoUrl(null)
                .build();
        ingredients.forEach(ingredient -> ingredient.setRecipe(expectedRecipe));

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(expectedRecipe);

        Recipe newRecipe = recipeService.updateRecipe(1L, updateRequest);

        assertThat(newRecipe.getTitle()).isEqualTo(expectedRecipe.getTitle());
        assertThat(newRecipe.getDescription()).isEqualTo(expectedRecipe.getDescription());
        assertThat(newRecipe.getImageUrl()).isEqualTo(expectedRecipe.getImageUrl());
        assertThat(newRecipe.getVideoUrl()).isEqualTo(expectedRecipe.getVideoUrl());
        assertThat(newRecipe.getIngredients()).isEqualTo(expectedRecipe.getIngredients());

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}