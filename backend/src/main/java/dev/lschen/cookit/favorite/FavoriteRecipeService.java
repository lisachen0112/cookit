package dev.lschen.cookit.favorite;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeRepository;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FavoriteRecipeService {

    RecipeRepository recipeRepository;
    FavoriteRecipeRepository favoriteRecipeRepository;

    public void favoriteRecipe(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        if (Objects.equals(user.getUsername(), recipe.getCreatedBy().getUsername())) {
            throw new RuntimeException("Cannot save user's own recipe");
        }

        if (favoriteRecipeRepository.recipeIsAlreadyFavorited(recipeId, user.getUsername())) {
            throw new RuntimeException("Recipe already favorited");
        }
        FavoriteRecipe favoriteRecipe = FavoriteRecipe.builder()
                .recipe(recipe)
                .favoritedBy(user)
                .build();

        favoriteRecipeRepository.save(favoriteRecipe);
    }
}
