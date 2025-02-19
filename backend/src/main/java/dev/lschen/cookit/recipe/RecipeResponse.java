package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;

import java.time.LocalDateTime;
import java.util.List;

public record RecipeResponse(
        Long recipeId,
        String title,
        String description,
        String imageUrl,
        String videoUrl,
        List<Ingredient> ingredients,
        List<Instruction> instructions,
        String createdBy,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
