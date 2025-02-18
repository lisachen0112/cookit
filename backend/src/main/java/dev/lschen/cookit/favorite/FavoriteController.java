package dev.lschen.cookit.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("recipes/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{recipe-id}")
    ResponseEntity<Void> addRecipeToFavorites(
            @PathVariable("recipe-id") Long recipeId,
            Authentication authentication) {
        favoriteService.addRecipeToFavorites(recipeId, authentication);
        return ResponseEntity.created(null).build();
    }


    @DeleteMapping("/{recipe-id}")
    ResponseEntity<Void> removeRecipeFromFavorites(
            @PathVariable("recipe-id") Long recipeId,
            Authentication authentication) {
        favoriteService.removeRecipeFromFavorites(recipeId, authentication);
        return ResponseEntity.noContent().build();
    }
}
