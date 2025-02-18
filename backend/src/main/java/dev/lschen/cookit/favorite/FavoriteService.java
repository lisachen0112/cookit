package dev.lschen.cookit.favorite;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeRepository;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoritedRecipeRepository;

    public void addRecipeToFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        if (Objects.equals(user.getUsername(), recipe.getCreatedBy().getUsername())) {
            throw new RuntimeException("Cannot save user's own recipe");
        }

        if (favoritedRecipeRepository.existsByRecipeAndFavoritedBy(recipe, user)) {
            throw new RuntimeException("Recipe already favorited");
        }
        Favorite favoriteRecipe = Favorite.builder()
                .recipe(recipe)
                .favoritedBy(user)
                .build();

        favoritedRecipeRepository.save(favoriteRecipe);
    }

    @Transactional
    public void removeRecipeFromFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        if (!favoritedRecipeRepository.existsByRecipeAndFavoritedBy(recipe, user)) {
            throw new RuntimeException("Recipe has to be added to favorites before it can be removed");
        }

        favoritedRecipeRepository.deleteByRecipeAndFavoritedBy(recipe, user);
    }
}
