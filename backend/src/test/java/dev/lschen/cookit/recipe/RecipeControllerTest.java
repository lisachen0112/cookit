package dev.lschen.cookit.recipe;

import dev.lschen.cookit.security.JwtFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static dev.lschen.cookit.utils.TestUtils.asJsonString;
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

    @MockitoBean
    private Authentication authentication;

    Recipe recipe;
    RecipeRequest request;
    RecipeResponse response;
    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .description("description")
                .build();

        request = new RecipeRequest("title",
                "description",
                "imageUrl",
                "videoUrl",
                null,
                null
        );
        response = new RecipeResponse(
                1L,
                "title",
                "description",
                "imageUrl",
                "videoUrl",
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    public void getAllRecipesEndpointTest() throws Exception {
        when(recipeService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(List.of(response))));

        verify(recipeService, times(1)).findAll();
    }

    @Test
    public void createRecipeEndpointTest() throws Exception {
        when(recipeService.createRecipe(any(RecipeRequest.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/recipes/1"));

        verify(recipeService, times(1)).createRecipe(any(RecipeRequest.class));
    }

    @Test
    public void getRecipeByIdEndpointTest() throws Exception {
        when(recipeService.findById(anyLong())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(response)));

        verify(recipeService, times(1)).findById(1L);
    }

    @Test
    public void deleteRecipeByIdEndpointTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/{id}", 1L))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(recipeService, times(1)).deleteById(1L);
    }

    @Test
    public void updateRecipeEndpointTest() throws Exception {
        when(recipeService.updateRecipe(anyLong(), any(RecipeRequest.class), any(Authentication.class)))
                .thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.patch("/recipes/{id}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(response)));

        verify(recipeService, times(1))
                .updateRecipe(anyLong(), any(RecipeRequest.class), any(Authentication.class));
    }
}