package dev.lschen.cookit.favorite;

import dev.lschen.cookit.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void addRecipeToFavoritesEndpoint() throws Exception {
        when(authentication.getName()).thenReturn("user");
        mockMvc.perform(MockMvcRequestBuilders.post("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andReturn();

        verify(favoriteService, times(1)).addRecipeToFavorites(anyLong(), any(Authentication.class));
    }

    @Test
    public void removeRecipeFromFavoritesEndpoint() throws Exception {
        when(authentication.getName()).thenReturn("user");
        mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/favorites/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent())
                .andReturn();
        verify(favoriteService, times(1)).removeRecipeFromFavorites(anyLong(), any(Authentication.class));
    }

}
