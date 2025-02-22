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
import static org.mockito.ArgumentMatchers.anyString;
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
                .username("principal")
                .build();
        queriedUser = User.builder()
                .username("queriedUser")
                .build();
        userPublicResponse = new UserPublicResponse(queriedUser.getUsername());
        userPrivateResponse = new UserPrivateResponse(
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
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserByUsername("queriedUser", authentication))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void getUserPublicInfoWhenTheRequesterQueriesOtherUserData() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(queriedUser));
        when(authentication.getPrincipal()).thenReturn(principal);
        when(userMapper.toUserPublicResponse(any(User.class))).thenReturn(userPublicResponse);

        UserResponse userResponse = userService.findUserByUsername("queriedUser", authentication);
        assertThat(userResponse).isInstanceOf(UserPublicResponse.class);
        assertThat(userResponse.username()).isEqualTo(queriedUser.getUsername());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userMapper, times(1)).toUserPublicResponse(any(User.class));
        verify(userMapper, never()).toUserPrivateResponse(any(User.class));
    }

    @Test
    public void getUserPrivateInfoWhenTheRequesterQueriesOwnData() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(principal));
        when(authentication.getPrincipal()).thenReturn(principal);
        when(userMapper.toUserPrivateResponse(any(User.class))).thenReturn(userPrivateResponse);

        UserResponse userResponse = userService.findUserByUsername("principal", authentication);
        assertThat(userResponse).isInstanceOf(UserPrivateResponse.class);
        assertThat(userResponse.username()).isEqualTo(principal.getUsername());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userMapper, never()).toUserPublicResponse(any(User.class));
        verify(userMapper, times(1)).toUserPrivateResponse(any(User.class));
    }

    @Test
    public void throwExceptionWhenQueringRecipeOfNonExistentUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findRecipesByUser(0, 10, "nonexistentUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findByUsername(anyString());
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

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(queriedUser));
        when(recipeService.findRecipesByUser(anyInt(), anyInt(), anyString())).thenReturn(response);

        PageResponse<RecipeResponse> results = userService.findRecipesByUser(0, 10,"queriedUser");

        assertThat(results).isEqualTo(response);

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(recipeService, times(1)).findRecipesByUser(anyInt(), anyInt(), anyString());
    }

    @Test
    public void throwExceptionWhenGettingFavoritedRecipesOfNonExistentUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findFavoritedRecipesByUser(0, 10,"nonexistentUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findByUsername(anyString());
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

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(queriedUser));
        when(favoriteService.findFavoritesByUser(anyInt(), anyInt(), anyString())).thenReturn(response);

        PageResponse<RecipeResponse> results = userService.findFavoritedRecipesByUser(0, 10,"queriedUser");

        assertThat(results).isEqualTo(response);
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(favoriteService, times(1)).findFavoritesByUser(anyInt(), anyInt(), anyString());
    }
}