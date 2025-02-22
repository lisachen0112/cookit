package dev.lschen.cookit.user;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.favorite.FavoriteService;
import dev.lschen.cookit.recipe.RecipeResponse;
import dev.lschen.cookit.recipe.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RecipeService recipeService;

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private UserService userService;

    private final Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
    private User principal;
    private User queriedUser;
    private UserPublicResponse userPublicResponse;
    private UserPrivateResponse userPrivateResponse;
    private List<RecipeResponse> recipeResponseList;

    @BeforeEach
    void setUp() {
        principal = User.builder()
                .userId(1L)
                .username("principal")
                .build();
        queriedUser = User.builder()
                .userId(2L)
                .username("queriedUser")
                .build();
        userPublicResponse = new UserPublicResponse(queriedUser.getUserId(), queriedUser.getUsername());
        userPrivateResponse = new UserPrivateResponse(
                principal.getUserId(),
                principal.getUsername(),
                null,
                null,
                null,
                null
        );

        recipeResponseList = new ArrayList<>();
        recipeResponseList.add(new RecipeResponse(
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ));
    }

    @Test
    public void throwExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserByUserId(2L, authentication))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getUserPublicInfoWhenTheRequesterQueriesOtherUserData() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(queriedUser));
        when(authentication.getPrincipal()).thenReturn(principal);
        when(userMapper.toUserPublicResponse(any(User.class))).thenReturn(userPublicResponse);

        UserResponse userResponse = userService.findUserByUserId(2L, authentication);
        assertThat(userResponse).isInstanceOf(UserPublicResponse.class);
        assertThat(userResponse.username()).isEqualTo(queriedUser.getUsername());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).toUserPublicResponse(any(User.class));
        verify(userMapper, never()).toUserPrivateResponse(any(User.class));
    }

    @Test
    public void getUserPrivateInfoWhenTheRequesterQueriesOwnData() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(principal));
        when(authentication.getPrincipal()).thenReturn(principal);
        when(userMapper.toUserPrivateResponse(any(User.class))).thenReturn(userPrivateResponse);

        UserResponse userResponse = userService.findUserByUserId(1L, authentication);
        assertThat(userResponse).isInstanceOf(UserPrivateResponse.class);
        assertThat(userResponse.username()).isEqualTo(principal.getUsername());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userMapper, never()).toUserPublicResponse(any(User.class));
        verify(userMapper, times(1)).toUserPrivateResponse(any(User.class));
    }

    @Test
    public void throwExceptionWhenQueringRecipeOfNonExistentUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findRecipesByUserId(0, 10, 3L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void successfullyGetRecipesByUser() {
        PageResponse<RecipeResponse> response = new PageResponse<>(
                recipeResponseList,
                1,
                1,
                0,
                10,
                true,
                true
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(queriedUser));
        when(recipeService.findRecipesByUserId(anyInt(), anyInt(), anyLong())).thenReturn(response);

        PageResponse<RecipeResponse> results = userService.findRecipesByUserId(0, 10, 2L);

        assertThat(results).isEqualTo(response);

        verify(userRepository, times(1)).findById(anyLong());
        verify(recipeService, times(1)).findRecipesByUserId(anyInt(), anyInt(), anyLong());
    }

    @Test
    public void throwExceptionWhenGettingFavoritedRecipesOfNonExistentUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findFavoritedRecipesByUserId(0, 10,3L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void successfullyGetFavoritedRecipesByUser() {
        PageResponse<RecipeResponse> response = new PageResponse<>(
                recipeResponseList,
                1,
                1,
                0,
                10,
                true,
                true
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(queriedUser));
        when(favoriteService.findFavoritesByUserId(anyInt(), anyInt(), anyLong())).thenReturn(response);

        PageResponse<RecipeResponse> results = userService.findFavoritedRecipesByUserId(0, 10,2L);

        assertThat(results).isEqualTo(response);
        verify(userRepository, times(1)).findById(anyLong());
        verify(favoriteService, times(1)).findFavoritesByUserId(anyInt(), anyInt(), anyLong());
    }
}