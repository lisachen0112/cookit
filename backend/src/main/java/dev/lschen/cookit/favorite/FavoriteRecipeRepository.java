package dev.lschen.cookit.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Long> {
    @Query("""
        SELECT (COUNT(*) > 0) AS isFavorited
        FROM FavoriteRecipe r
        WHERE r.favoritedBy.username = :username
        AND r.recipe.recipeId = :recipeId
        """)
    boolean recipeIsAlreadyFavorited(Long recipeId, String username);
}
