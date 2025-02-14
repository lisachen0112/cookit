package dev.lschen.cookit.ingredient;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record IngredientRequest(
        @NotNull(message = "Ingredient name cannot be empty")
        @NotEmpty(message = "Ingredient name cannot be empty")
        String name,

        @NotNull(message = "Quantity is required")
        @NotEmpty(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Measurement is required")
        @NotEmpty(message = "Measurement is required")
        String measurement
) {
}
