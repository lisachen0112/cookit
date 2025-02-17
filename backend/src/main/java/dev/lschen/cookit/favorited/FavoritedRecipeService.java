package dev.lschen.cookit.favorited;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeRepository;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FavoritedRecipeService {

    RecipeRepository recipeRepository;
    FavoritedRecipeRepository favoritedRecipeRepository;

    public void addRecipeToFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        if (Objects.equals(user.getUsername(), recipe.getCreatedBy().getUsername())) {
            throw new RuntimeException("Cannot save user's own recipe");
        }

        if (favoritedRecipeRepository.recipeIsAlreadyFavorited(recipeId, user.getUsername())) {
            throw new RuntimeException("Recipe already favorited");
        }
        FavoritedRecipe favoriteRecipe = FavoritedRecipe.builder()
                .recipe(recipe)
                .favoritedBy(user)
                .build();

        favoritedRecipeRepository.save(favoriteRecipe);
    }

    public void removeRecipeFromFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        if (!favoritedRecipeRepository.recipeIsAlreadyFavorited(recipeId, user.getUsername())) {
            throw new RuntimeException("Recipe has to be added to favorites before it can be removed");
        }
    }
}
