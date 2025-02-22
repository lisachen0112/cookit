package dev.lschen.cookit.user;


import dev.lschen.cookit.common.PageResponse;
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
    private PageResponse<RecipeResponse> pageResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserPublicResponse(1L, "test1");
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
        List<RecipeResponse> recipes = List.of(recipeResponse);
        pageResponse = new PageResponse<>(
                recipes,
                0,
                10,
                1,
                1,
                true,
                true);

    }

    @Test
    public void shouldReturnOkWhenRetrievingUserDetailsSuccessfully() throws Exception {
        when(userService.findUserByUserId(anyLong(), any(Authentication.class)))
                .thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 1L)
                        .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(userResponse)));

        verify(userService, times(1)).findUserByUserId(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnNotFoundWhenRetrievingNonexistentUser() throws Exception {
        String errorMsg = "User not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        when(userService.findUserByUserId(anyLong(), any(Authentication.class)))
                .thenThrow(new EntityNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(userService, times(1)).findUserByUserId(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnOkWhenRetrievingUserRecipeSuccessfully() throws Exception {
        when(userService.findRecipesByUserId(anyInt(), anyInt(), anyLong())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/recipes", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(pageResponse)));

        verify(userService, times(1)).findRecipesByUserId(anyInt(), anyInt(), anyLong());
    }

    @Test
    public void shouldReturnOkWhenRetrievingUserFavoritedRecipesSuccessfully() throws Exception {
        when(userService.findFavoritedRecipesByUserId(anyInt(), anyInt(), anyLong())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/favorites", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(pageResponse)));

        verify(userService, times(1)).findFavoritedRecipesByUserId(anyInt(), anyInt(), anyLong());
    }

}
