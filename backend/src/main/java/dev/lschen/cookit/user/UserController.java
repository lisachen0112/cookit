package dev.lschen.cookit.user;

import dev.lschen.cookit.recipe.RecipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserInfo(
            @PathVariable("username") String username,
            Authentication authentication) {
        return ResponseEntity.ok(userService.findUserByUsername(username, authentication));
    }

    @GetMapping("/{username}/recipes")
    public ResponseEntity<List<RecipeResponse>> getRecipes(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findRecipesByUser(username));
    }

    @GetMapping("/{username}/favorites")
    public ResponseEntity<List<RecipeResponse>> getFavoritedRecipes(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findFavoritedRecipesByUser(username));
    }

}
