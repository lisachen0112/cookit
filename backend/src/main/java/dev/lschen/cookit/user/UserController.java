package dev.lschen.cookit.user;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.recipe.RecipeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name="User")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserInfo(
            @PathVariable("userId") Long userId,
            Authentication authentication) {
        return ResponseEntity.ok(userService.findUserByUserId(userId, authentication));
    }

    @GetMapping("/{username}/recipes")
    public ResponseEntity<PageResponse<RecipeResponse>> getRecipes(
            @PathVariable("username") Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(userService.findRecipesByUserId(page, size, userId));
    }

    @GetMapping("/{username}/favorites")
    public ResponseEntity<PageResponse<RecipeResponse>> getFavoritedRecipes(
            @PathVariable("username") Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(userService.findFavoritedRecipesByUserId(page, size, userId));
    }

}
