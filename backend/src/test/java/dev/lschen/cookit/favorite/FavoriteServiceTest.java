package dev.lschen.cookit.favorite;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoritedService;

    @Mock
    private RecipeService recipeService;

    @Mock
    private FavoriteRepository favoritedRepository;

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
    public void ThrowExceptionWhenFavouringOwnRecipe() {
        recipe.setCreatedBy(mockUser);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);

        assertThatThrownBy(() -> favoritedService.addRecipeToFavorites(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot save user's own recipe");

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
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
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);
        when(favoritedRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(true);

        assertThatThrownBy(() -> favoritedService.addRecipeToFavorites(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Recipe already favorited");

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
        verify(favoritedRepository, times(1)).existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verifyNoMoreInteractions(favoritedRepository);
    }

    @Test
    public void SuccessfullyAddedRecipeToFavorites() {
        User newUser = User.builder()
                .username("newUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        recipe.setCreatedBy(newUser);

        Favorite expected = Favorite.builder()
                .recipe(recipe)
                .favoritedBy(mockUser)
                .build();

        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);
        when(favoritedRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(false);

        Favorite result = favoritedService.addRecipeToFavorites(1L, authentication);

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
        verify(favoritedRepository, times(1)).existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(favoritedRepository, times(1)).save(any(Favorite.class));
        assertThat(result.getFavoritedBy()).isEqualTo(expected.getFavoritedBy());
        assertThat(result.getRecipe()).isEqualTo(expected.getRecipe());
    }

    @Test
    public void ThrowExceptionWhenRemovingRecipeFromFavoritesIfItHasNotBeenFavoritedBeforehand() {
        when(favoritedRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(false);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);

        assertThatThrownBy(() -> favoritedService.removeRecipeFromFavorites(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Recipe has to be added to favorites before it can be removed");

        verify(favoritedRepository, times(1)).existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
    }

    @Test
    public void SuccessfullyRemovedRecipeFromFavorites() {
        when(favoritedRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(true);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);

        favoritedService.removeRecipeFromFavorites(1L, authentication);

        verify(favoritedRepository, times(1)).
                existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(favoritedRepository, times(1))
                .deleteByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
    }
}