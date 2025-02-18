package dev.lschen.cookit.favorite;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByRecipeAndFavoritedBy(Recipe recipe, User favoritedBy);

    void deleteByRecipeAndFavoritedBy(Recipe recipe, User user);
}
