package dev.lschen.cookit.instruction;

import dev.lschen.cookit.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructionRepository extends JpaRepository<Instruction, Long> {
    void deleteByRecipe(Recipe recipe);
}
