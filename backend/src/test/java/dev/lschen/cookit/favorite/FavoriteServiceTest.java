package dev.lschen.cookit.favorite;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeRepository;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteRecipeService favoriteService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private FavoriteRecipeRepository favoriteRepository;

    Authentication authentication;
    Recipe recipe;
    User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .username("testUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);

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
    }

    @Test
    public void ThrowExceptionWhenFavouringNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.favoriteRecipe(1L, authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    public void ThrowExceptionWhenFavouringOwnRecipe() {
        recipe.setCreatedBy(mockUser);
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> favoriteService.favoriteRecipe(1L, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot save user's own recipe");

        verify(recipeRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    public void ThrowExceptionWhenFavouringAlreadyFavoritedRecipe() {
        User newUser = User.builder()
                .username("newUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        recipe.setCreatedBy(newUser);
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(favoriteRepository.recipeIsAlreadyFavorited(anyLong(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.favoriteRecipe(1L, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Recipe already favorited");

        verify(recipeRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(recipeRepository);
        verify(favoriteRepository, times(1)).recipeIsAlreadyFavorited(anyLong(), anyString());
        verifyNoMoreInteractions(favoriteRepository);
    }

    @Test
    public void SuccessfullyFavoriteRecipe() {
        User newUser = User.builder()
                .username("newUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        recipe.setCreatedBy(newUser);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(favoriteRepository.recipeIsAlreadyFavorited(anyLong(), anyString())).thenReturn(false);

        favoriteService.favoriteRecipe(1L, authentication);

        verify(recipeRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(recipeRepository);
        verify(favoriteRepository, times(1)).recipeIsAlreadyFavorited(anyLong(), anyString());
        verify(favoriteRepository, times(1)).save(any(FavoriteRecipe.class));
    }

    @Test
    public void ThrowExceptionWhenUnfavoringRecipeThatWasNotFavorited() {
        when(favoriteRepository.recipeIsAlreadyFavorited(anyLong(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> favoriteService.unfavoriteRecipe(1L, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Recipe has not been favorited beforehand");

        verify(favoriteRepository, times(1)).recipeIsAlreadyFavorited(anyLong(), anyString());
    }

}