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
import static org.mockito.Mockito.*;
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
    RecipeRequest request;

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
    public void getAllRecipesEndpointTest() throws Exception {

        when(recipeService.findAll()).thenReturn(List.of(recipe));

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void createRecipeEndpointTest() throws Exception {

        when(recipeService.createRecipe(any(RecipeRequest.class))).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andReturn();
    }

//    @Test
//    public void updateRecipeEndpointTest() throws Exception {
//
//
//        mockMvc.perform(MockMvcRequestBuilders.patch("/recipes/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(request)))
//                .andExpect(status().isOk())
//                .andReturn();
//    }

    @Test
    public void getRecipeByIdEndpointTest() throws Exception {

        when(recipeService.findById(anyLong())).thenReturn(recipe);

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(recipe)));

        verify(recipeService, times(1)).findById(1L);
    }

    @Test
    public void deleteRecipeByIdEndpointTest() throws Exception {

        doNothing().when(recipeService).deleteById(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/{id}", 1L))
                .andExpect(status().isAccepted())
                .andReturn();

        verify(recipeService, times(1)).deleteById(1L);
    }
}