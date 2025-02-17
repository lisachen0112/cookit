package dev.lschen.cookit.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> searchByTitleOrDescription(@Param("keyword") String keyword);
}
