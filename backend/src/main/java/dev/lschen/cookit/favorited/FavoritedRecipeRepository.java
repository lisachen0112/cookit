package dev.lschen.cookit.favorited;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritedRecipeRepository extends JpaRepository<FavoritedRecipe, Long> {

    boolean existsByRecipeAndFavoritedBy(Recipe recipe, User favoritedBy);

    void deleteByRecipeAndFavoritedBy(Recipe recipe, User user);
}
