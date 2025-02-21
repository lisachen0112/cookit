package dev.lschen.cookit.user;


import dev.lschen.cookit.handler.ExceptionResponse;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private Authentication authentication;

    @Autowired
    private MockMvc mockMvc;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserPublicResponse("test");
    }

    @Test
    public void shouldReturnOkWhenRetrievingUserDetailsSuccessfully() throws Exception {
        when(userService.findUserByUsername(anyString(), any(Authentication.class)))
                .thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", "test")
                        .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(userResponse)));

        verify(userService, times(1)).findUserByUsername(anyString(), any(Authentication.class));
    }

    @Test
    public void shouldReturnNotFoundWhenRetrievingNonexistentUser() throws Exception {
        String errorMsg = "User not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        when(userService.findUserByUsername(anyString(), any(Authentication.class)))
                .thenThrow(new EntityNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", "test")
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(userService, times(1)).findUserByUsername(anyString(), any(Authentication.class));
    }

    @Test
    public void shouldReturnOkWhenRetrievingUserOwnRecipeSuccessfully() throws Exception {
        RecipeResponse recipeResponse = new RecipeResponse(
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        List<RecipeResponse> response = List.of(recipeResponse);
        when(userService.findRecipesByUser(anyString())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/recipes", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(response)));

        verify(userService, times(1)).findRecipesByUser(anyString());
    }

    @Test
    public void shouldReturnOkWhenRetrievingUserFavoritedRecipesSuccessfully() throws Exception {
        RecipeResponse recipeResponse = new RecipeResponse(
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        List<RecipeResponse> response = List.of(recipeResponse);
        when(userService.findFavoritedRecipesByUser(anyString())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/favorites", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(response)));

        verify(userService, times(1)).findFavoritedRecipesByUser(anyString());
    }

}
