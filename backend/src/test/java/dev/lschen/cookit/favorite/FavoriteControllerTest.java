package dev.lschen.cookit.favorite;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.handler.ExceptionResponse;
import dev.lschen.cookit.security.JwtFilter;
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

import static dev.lschen.cookit.utils.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(FavoriteController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private Authentication authentication;

    @MockitoBean
    JwtFilter jwtService;

    @Test
    public void shouldReturnCreatedWhenAddingToFavoritesSuccessfully() throws Exception {
        Favorite favorite = Favorite.builder()
                .id(1L)
                .build();
        when(favoriteService.addRecipeToFavorites(anyLong(), any(Authentication.class))).thenReturn(favorite);

    mockMvc.perform(MockMvcRequestBuilders.post("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/recipes/favorites/1"));

        verify(favoriteService, times(1)).addRecipeToFavorites(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnBadRequestWhenAddindToFavoritesOwnRecipe() throws Exception {
        String errorMsg = "Cannot save user's own recipe";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        when(favoriteService.addRecipeToFavorites(anyLong(), any(Authentication.class)))
                .thenThrow(new OperationNotPermittedException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.post("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(favoriteService, times(1))
                .addRecipeToFavorites(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnBadRequestWhenRecipeAlreadyFavorited() throws Exception {
        String errorMsg = "Recipe already favorited";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        when(favoriteService.addRecipeToFavorites(anyLong(), any(Authentication.class)))
                .thenThrow(new OperationNotPermittedException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.post("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(favoriteService, times(1))
                .addRecipeToFavorites(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnNoContentWhenRemovingFromFavoritesSuccessfully() throws Exception {
        doNothing().when(favoriteService).removeRecipeFromFavorites(anyLong(), any(Authentication.class));
        mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent())
                .andReturn();
        verify(favoriteService, times(1)).removeRecipeFromFavorites(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnBadRequestWhenRemovingFromFavoritesRecipeThatHasNotBeenFavoritedBefore() throws Exception {
        String errorMsg = "Recipe has to be added to favorites before it can be removed";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        doThrow(new OperationNotPermittedException(errorMsg))
                .when(favoriteService).removeRecipeFromFavorites(anyLong(), any(Authentication.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(favoriteService, times(1)).removeRecipeFromFavorites(anyLong(), any(Authentication.class));
    }

}
