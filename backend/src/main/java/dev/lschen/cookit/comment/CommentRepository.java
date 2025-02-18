package dev.lschen.cookit.comment;

import dev.lschen.cookit.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRecipe(Recipe any);
}
