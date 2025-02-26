package dev.lschen.cookit.favorite;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeMapper;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private RecipeMapper recipeMapper;

    @Mock
    private FavoriteRepository favoriteRepository;

    Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
    Recipe recipe;
    User mockUser;
    Favorite favorite;
    RecipeResponse recipeResponse;
    User newUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .userId(1L)
                .username("testUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

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
                .content("ingredient")
                .recipe(recipe)
                .build());

        favorite = Favorite.builder()
                .favoritedBy(mockUser)
                .recipe(recipe)
                .build();

        recipeResponse = new RecipeResponse(
                1L,
                "title",
                "description",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        newUser = User.builder()
                .userId(2L)
                .username("newUser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    public void ThrowExceptionWhenFavouringOwnRecipe() {
        recipe.setCreatedBy(mockUser);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);

        assertThatThrownBy(() -> favoritedService.addRecipeToFavorites(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot save user's own recipe");

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
    }

    @Test
    public void ThrowExceptionWhenFavouringAlreadyFavoritedRecipe() {
        recipe.setCreatedBy(newUser);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);
        when(favoriteRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(true);

        assertThatThrownBy(() -> favoritedService.addRecipeToFavorites(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Recipe already favorited");

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
        verify(favoriteRepository, times(1)).existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verifyNoMoreInteractions(favoriteRepository);
    }

    @Test
    public void SuccessfullyAddedRecipeToFavorites() {
        recipe.setCreatedBy(newUser);

        Favorite expected = Favorite.builder()
                .recipe(recipe)
                .favoritedBy(mockUser)
                .build();

        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);
        when(favoriteRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(false);

        Favorite result = favoritedService.addRecipeToFavorites(1L, authentication);

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
        verify(favoriteRepository, times(1)).existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
        assertThat(result.getFavoritedBy()).isEqualTo(expected.getFavoritedBy());
        assertThat(result.getRecipe()).isEqualTo(expected.getRecipe());
    }

    @Test
    public void ThrowExceptionWhenRemovingRecipeFromFavoritesIfItHasNotBeenFavoritedBeforehand() {
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(favoriteRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(false);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);

        assertThatThrownBy(() -> favoritedService.removeRecipeFromFavorites(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Recipe has to be added to favorites before it can be removed");

        verify(favoriteRepository, times(1)).existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
    }

    @Test
    public void SuccessfullyRemovedRecipeFromFavorites() {
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(favoriteRepository.existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class))).thenReturn(true);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);

        favoritedService.removeRecipeFromFavorites(1L, authentication);

        verify(favoriteRepository, times(1)).
                existsByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(favoriteRepository, times(1))
                .deleteByRecipeAndFavoritedBy(any(Recipe.class), any(User.class));
        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
    }

    @Test
    public void SuccessfullyGetFavoritesByUser() {
        Page<Favorite> recipePage = new PageImpl<>(List.of(favorite), PageRequest.of(0, 10), 1);
        when(favoriteRepository.findByFavoritedBy_UserId(any(Pageable.class), anyLong()))
                .thenReturn(recipePage);
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(recipeResponse);

        PageResponse<RecipeResponse> results = favoritedService.findFavoritesByUserId(0, 10,1L);

        assertThat(results.getContent()).isEqualTo(List.of(recipeResponse));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);

        verify(favoriteRepository, times(1))
                .findByFavoritedBy_UserId(any(Pageable.class), anyLong());
    }
}