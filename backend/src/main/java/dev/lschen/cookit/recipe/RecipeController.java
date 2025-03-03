package dev.lschen.cookit.recipe;

import dev.lschen.cookit.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("recipes")
@RequiredArgsConstructor
@Tag(name="Recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    ResponseEntity<PageResponse<RecipeListResponse>> getAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(recipeService.findAll(page, size));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> createRecipe(@ModelAttribute @Valid RecipeRequest recipe) throws IOException {
        RecipeResponse response = recipeService.createRecipe(recipe);
        return ResponseEntity.created(URI.create("/recipes/" + response.recipeId())).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            Authentication authentication
    ) {
        recipeService.deleteById(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> updateRecipe(
            @PathVariable Long id,
            @ModelAttribute RecipeRequest request,
            Authentication authentication) throws IOException {
        return ResponseEntity.ok(recipeService.updateRecipe(id, request, authentication));
    }
}