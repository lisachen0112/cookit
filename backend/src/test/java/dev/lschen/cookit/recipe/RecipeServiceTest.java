package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
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
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;

    private RecipeRequest request;

    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .description("description")
                .createdBy(null)
                .createdDate(null)
                .lastModifiedDate(null)
                .ingredients(List.of())
                .imageUrl(null)
                .videoUrl(null)
                .build();

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .ingredientId(1L)
                .name("ingredient")
                .quantity(10)
                .measurement("measurement")
                .recipe(recipe)
                .build());

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
}