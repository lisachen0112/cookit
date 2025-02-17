package dev.lschen.cookit.recipe;

import dev.lschen.cookit.ingredient.Ingredient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record RecipeRequest (

        @NotNull(message = "Title is required")
        @NotEmpty(message = "Title is required")
        String title,
        String description,
        String imageUrl,
        String videoUrl,

        @NotEmpty(message = "Add at least one ingredient")
        @Valid
        List<Ingredient> ingredients
) {
}
