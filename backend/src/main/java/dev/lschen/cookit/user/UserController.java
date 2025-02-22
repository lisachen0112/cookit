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

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserInfo(
            @PathVariable("username") String username,
            Authentication authentication) {
        return ResponseEntity.ok(userService.findUserByUsername(username, authentication));
    }

    @GetMapping("/{username}/recipes")
    public ResponseEntity<PageResponse<RecipeResponse>> getRecipes(
            @PathVariable("username") String username,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(userService.findRecipesByUser(page, size, username));
    }

    @GetMapping("/{username}/favorites")
    public ResponseEntity<PageResponse<RecipeResponse>> getFavoritedRecipes(
            @PathVariable("username") String username,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(userService.findFavoritedRecipesByUser(page, size, username));
    }

}
