package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.security.JwtFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static dev.lschen.cookit.utils.TestUtils.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RecipeController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    RecipeService recipeService;

    @MockitoBean
    JwtFilter jwtService;

    Recipe recipe;

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
    }

    @Test
    public void getAllRecipesEndpointTest() throws Exception {

        when(recipeService.findAll()).thenReturn(List.of(recipe));

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void createRecipeEndpointTest() throws Exception {

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                        .ingredientId(1L)
                        .name("ingredient")
                        .quantity(10)
                        .measurement("measurement")
                        .recipe(recipe)
                .build());
        RecipeRequest request = new RecipeRequest("title",
                "description",
                "imageURL",
                "videoUrl",
                ingredients
                );

        when(recipeService.createRecipe(any(RecipeRequest.class))).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andReturn();
    }
}