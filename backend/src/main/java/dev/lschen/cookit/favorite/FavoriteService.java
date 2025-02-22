package dev.lschen.cookit.favorite;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeMapper;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoritedRecipeRepository;
    private final RecipeService recipeService;
    private final FavoriteRepository favoriteRepository;
    private final RecipeMapper recipeMapper;

    public Favorite addRecipeToFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeService.findRecipeOrThrowException(recipeId);

        if (Objects.equals(user.getUserId(), recipe.getCreatedBy().getUserId())) {
            throw new OperationNotPermittedException("Cannot save user's own recipe");
        }

        if (favoritedRecipeRepository.existsByRecipeAndFavoritedBy(recipe, user)) {
            throw new OperationNotPermittedException("Recipe already favorited");
        }
        Favorite favoriteRecipe = Favorite.builder()
                .recipe(recipe)
                .favoritedBy(user)
                .build();

        favoritedRecipeRepository.save(favoriteRecipe);
        return favoriteRecipe;
    }

    @Transactional
    public void removeRecipeFromFavorites(Long recipeId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Recipe recipe = recipeService.findRecipeOrThrowException(recipeId);

        if (!favoritedRecipeRepository.existsByRecipeAndFavoritedBy(recipe, user)) {
            throw new OperationNotPermittedException("Recipe has to be added to favorites before it can be removed");
        }

        favoritedRecipeRepository.deleteByRecipeAndFavoritedBy(recipe, user);
    }

    public PageResponse<RecipeResponse> findFavoritesByUserId(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("favoritedAt").descending());
        Page<Favorite> favorites = favoriteRepository.findByFavoritedBy_UserId(pageable, userId);

        List<RecipeResponse> response = favorites.stream()
                .map(fav-> recipeMapper.toRecipeResponse(fav.getRecipe()))
                .toList();

        return new PageResponse<>(
                response,
                favorites.getNumber(),
                favorites.getSize(),
                favorites.getTotalElements(),
                favorites.getTotalPages(),
                favorites.isFirst(),
                favorites.isLast()
        );
    }
}
