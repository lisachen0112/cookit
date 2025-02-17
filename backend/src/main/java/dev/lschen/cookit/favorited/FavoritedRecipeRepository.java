package dev.lschen.cookit.favorited;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoritedRecipeRepository extends JpaRepository<FavoritedRecipe, Long> {
    @Query("""
        SELECT (COUNT(*) > 0) AS isFavorited
        FROM FavoritedRecipe r
        WHERE r.favoritedBy.username = :username
        AND r.recipe.recipeId = :recipeId
        """)
    boolean recipeIsAlreadyFavorited(Long recipeId, String username);
}
