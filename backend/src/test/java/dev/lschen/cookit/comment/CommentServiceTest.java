package dev.lschen.cookit.comment;

import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    Comment comment;
    CommentRequest request;
    Recipe recipe;

    @BeforeEach
    void setUp() {
        request = new CommentRequest("comment");
        comment = Comment.builder()
                .commentId(1L)
                .content("content")
                .build();
        recipe = Recipe.builder()
                .title("title")
                .build();
    }

    @Test
    public void successfulAddComment() {
        when(recipeService.findById(anyLong())).thenReturn(recipe);
        when(commentRepository.save(any())).thenReturn(comment);

        Comment comment = commentService.addComment(request,1L);
        assertThat(comment.getContent()).isEqualTo("content");

        verify(recipeService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(recipeService);
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

        Comment result = commentService.findById(1L);
        assertThat(result).isEqualTo(comment);

        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    public void successfulUpdateComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.patchById(request,1L);
        assertThat("comment").isEqualTo(result.getContent());

        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void successfulDeleteComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.deleteById(1L);
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    public void successfulGetAllCommentsForRecipe() {
        when(recipeService.findById(anyLong())).thenReturn(recipe);
        when(commentRepository.findByRecipe(any(Recipe.class))).thenReturn(List.of(comment));

        List<Comment> result = commentService.getAllCommentsForRecipe(1L);
        assertThat(result).isEqualTo(List.of(comment));

        verify(recipeService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(recipeService);
        verify(commentRepository, times(1)).findByRecipe(any(Recipe.class));
        verifyNoMoreInteractions(commentRepository);
    }



}