package dev.lschen.cookit.recipe;

public record RecipeListResponse(
        Long recipeId,
        String title,
        String description,
        String imageUrl,
        String videoUrl,
        String createdBy
) {
}
