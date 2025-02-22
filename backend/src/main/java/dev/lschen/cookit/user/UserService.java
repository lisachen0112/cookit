package dev.lschen.cookit.user;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.favorite.FavoriteService;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RecipeService recipeService;
    private final FavoriteService favoriteService;

    public User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserResponse findUserByUserId(Long userId, Authentication authentication) {
        User queriedUser = getUserOrThrowException(userId);
        User principal = (User) authentication.getPrincipal();
        if (principal.getUserId().equals(queriedUser.getUserId())) {
            return userMapper.toUserPrivateResponse(queriedUser);
        }
        return userMapper.toUserPublicResponse(queriedUser);
    }

    public PageResponse<RecipeResponse> findRecipesByUserId(int page, int size, Long userId) {
        getUserOrThrowException(userId);
        return recipeService.findRecipesByUserId(page, size, userId);
    }

    public PageResponse<RecipeResponse> findFavoritedRecipesByUserId(int page, int size, Long userId) {
        getUserOrThrowException(userId);
        return favoriteService.findFavoritesByUserId(page, size, userId);
    }

}
