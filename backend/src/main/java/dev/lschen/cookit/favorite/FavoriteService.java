package dev.lschen.cookit.favorite;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoritedRecipeRepository;
    private final RecipeService recipeService;

    public Favorite addRecipeToFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeService.findRecipeOrThrowException(recipeId);

        if (Objects.equals(user.getUsername(), recipe.getCreatedBy().getUsername())) {
            throw new OperationNotPermittedException("Cannot save user's own recipe");
        }

        if (favoritedRecipeRepository.existsByRecipeAndFavoritedBy(recipe, user)) {
            throw new OperationNotPermittedException("Recipe already favorited");
        }
        Favorite favoriteRecipe = Favorite.builder()
                .recipe(recipe)
                .favoritedBy(user)
                .build();

        favoritedRecipeRepository.save(favoriteRecipe);
        return favoriteRecipe;
    }

    @Transactional
    public void removeRecipeFromFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeService.findRecipeOrThrowException(recipeId);

        if (!favoritedRecipeRepository.existsByRecipeAndFavoritedBy(recipe, user)) {
            throw new OperationNotPermittedException("Recipe has to be added to favorites before it can be removed");
        }

        favoritedRecipeRepository.deleteByRecipeAndFavoritedBy(recipe, user);
    }

    // TODO
    public List<RecipeResponse> findFavoritesByUser(String username) {
        return null;
    }
}
