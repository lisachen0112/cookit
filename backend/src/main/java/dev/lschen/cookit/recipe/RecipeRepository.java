package dev.lschen.cookit.recipe;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findAll(@NonNull Pageable pageable);
    Page<Recipe> findByCreatedBy_Username(Pageable pageable, String username);
}
