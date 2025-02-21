package dev.lschen.cookit.user;

import dev.lschen.cookit.favorite.FavoriteService;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RecipeService recipeService;
    private final FavoriteService favoriteService;

    public User getUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserResponse findUserByUsername(String username, Authentication authentication) {
        User queriedUser = getUserOrThrowException(username);
        User principal = (User) authentication.getPrincipal();
        if (principal.getUsername().equals(queriedUser.getUsername())) {
            return userMapper.toUserPrivateResponse(queriedUser);
        }
        return userMapper.toUserPublicResponse(queriedUser);
    }

    public List<RecipeResponse> findRecipesByUser(String username) {
        getUserOrThrowException(username);
        return recipeService.findRecipesByUser(username);
    }

    public List<RecipeResponse> findFavoritedRecipesByUser(String username) {
        getUserOrThrowException(username);
        return favoriteService.findFavoritesByUser(username);
    }

}
