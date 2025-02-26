package dev.lschen.cookit.recipe;

import org.springframework.stereotype.Service;

@Service
public class RecipeMapper {

    public Recipe toRecipe(RecipeRequest request) {
        return Recipe.builder()
                .title(request.title())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .videoUrl(request.videoUrl())
                .build();
    }

    public RecipeResponse toRecipeResponse(Recipe recipe) {
        return new RecipeResponse(
                recipe.getRecipeId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getImageUrl(),
                recipe.getVideoUrl(),
                recipe.getIngredients(),
                recipe.getInstructions(),
                recipe.getCreatedBy().getUsername(),
                recipe.getCreatedDate(),
                recipe.getLastModifiedDate()
        );
    }

    public RecipeListResponse toRecipeListResponse(Recipe recipe) {
        return new RecipeListResponse(
                recipe.getRecipeId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getImageUrl(),
                recipe.getCreatedBy().getUsername()
        );
    }
}
