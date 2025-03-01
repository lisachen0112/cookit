package dev.lschen.cookit.comment;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RecipeService recipeService;

    @Mock
    private CommentMapper commentMapper;

    Comment comment;
    CommentRequest request;
    Recipe recipe;
    Authentication authentication;
    User user;
    CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .username("test1")
                .build();

        request = new CommentRequest("new comment");
        comment = Comment.builder()
                .commentId(1L)
                .content("new comment")
                .build();
        recipe = Recipe.builder()
                .title("title")
                .build();

        commentResponse = new CommentResponse(
                1L,
                "new comment",
                null,
                null,
                null
        );
    }

    @Test
    public void throwErrorWhenAddingCommentOnNonExistentRecipe() {
        when(recipeService.findRecipeOrThrowException(anyLong())).thenThrow(new EntityNotFoundException("Recipe not found"));

        assertThatThrownBy(() -> commentService.addComment(request, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Recipe not found");

        verifyNoInteractions(commentRepository);
    }

    @Test
    public void successfulAddComment() {
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);
        when(commentMapper.toComment(any(CommentRequest.class), any(Recipe.class))).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentResponse(comment)).thenReturn(commentResponse);

        CommentResponse response = commentService.addComment(request,1L);
        assertThat(response.content()).isEqualTo("new comment");

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void throwErrorWhenGettingNonExistentComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment not found");

        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getCommentByIdTest() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentResponse(comment)).thenReturn(commentResponse);

        CommentResponse response = commentService.findById(1L);
        assertThat(response).isEqualTo(commentResponse);

        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentMapper, times(1)).toCommentResponse(comment);
    }

    @Test
    public void throwErrorWhenUpdatingNonExistentComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.patchById(request, 1L, authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Comment not found");

        verify(commentRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void throwErrorWhenUpdatingOtherUsersComment() {
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(user);
        User otherUser = User.builder()
                .userId(2L)
                .username("test2")
                .build();
        comment.setCommentedBy(otherUser);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.patchById(request,1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot update other users comment");

        verify(commentRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void successfulUpdateComment() {
        CommentRequest updateRequest = new CommentRequest("updated comment");
        CommentResponse updatedCommentResponse = new CommentResponse(
                1L,
                "updated comment",
                null,
                null,
                null
        );
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        comment.setCommentedBy(user);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(authentication.getPrincipal()).thenReturn(user);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(commentMapper.toCommentResponse(any(Comment.class)))
                .thenReturn(updatedCommentResponse);

        CommentResponse response = commentService.patchById(updateRequest,1L, authentication);
        assertThat("updated comment").isEqualTo(response.content());

        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void throwErrorWhenDeletingNonExistentComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentService.deleteById(1L, authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Comment not found");

        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    public void throwErrorWhenDeletingOtherUsersComment() {
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(authentication.getPrincipal()).thenReturn(user);
        User otherUser = User.builder()
                .userId(2L)
                .username("test2")
                .build();
        comment.setCommentedBy(otherUser);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteById(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot delete other users comment");

        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    public void successfulDeleteComment() {
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        comment.setCommentedBy(user);

        when(authentication.getPrincipal()).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        commentService.deleteById(1L, authentication);

        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void throwErrorWhenRetrievingAllCommentsOnNonExistentRecipe() {
        when(recipeService.findRecipeOrThrowException(anyLong()))
                .thenThrow(new EntityNotFoundException("Recipe not found"));

        assertThatThrownBy(() -> commentService.getAllCommentsForRecipe(1L, 0, 10))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Recipe not found");

        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
    }

    @Test
    public void successfulGetAllCommentsForRecipe() {
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), PageRequest.of(0, 10), 1);
        when(recipeService.findRecipeOrThrowException(anyLong())).thenReturn(recipe);
        when(commentRepository.findByRecipe_RecipeId(any(Pageable.class), anyLong())).thenReturn(commentPage);
        when(commentMapper.toCommentResponse(any(Comment.class))).thenReturn(commentResponse);

        PageResponse<CommentResponse> results = commentService.getAllCommentsForRecipe(1L, 0, 10);

        assertThat(results.getContent()).isEqualTo(List.of(commentResponse));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);


        verify(recipeService, times(1)).findRecipeOrThrowException(anyLong());
        verifyNoMoreInteractions(recipeService);
        verify(commentRepository, times(1)).findByRecipe_RecipeId(any(Pageable.class), anyLong());
    }
}