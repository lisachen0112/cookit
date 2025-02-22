package dev.lschen.cookit.favorite;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByRecipeAndFavoritedBy(Recipe recipe, User favoritedBy);

    void deleteByRecipeAndFavoritedBy(Recipe recipe, User user);

    Page<Favorite> findByFavoritedBy_UserId(Pageable pageable, Long userId);
}
