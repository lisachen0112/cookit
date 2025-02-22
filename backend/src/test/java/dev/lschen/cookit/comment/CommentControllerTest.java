package dev.lschen.cookit.comment;

import dev.lschen.cookit.common.PageResponse;
import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.handler.ExceptionResponse;
import dev.lschen.cookit.security.JwtFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static dev.lschen.cookit.utils.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    JwtFilter jwtService;

    @MockitoBean
    CommentService commentService;

    @MockitoBean
    private Authentication authentication;

    Comment comment;
    CommentRequest request;
    CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        request = new CommentRequest("comment");
        comment = Comment.builder()
                .commentId(1L)
                .content("content")
                .build();
        commentResponse = new CommentResponse(
                1L,
                "how many grams of pasta?",
                null,
                null,
                null
        );
    }

    @Test
    public void shouldReturnOkWhenRetrievingCommentByIdSuccessfully() throws Exception {
        when(commentService.findById(anyLong())).thenReturn(commentResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(commentResponse)));
        verify(commentService, times(1)).findById(anyLong());
    }

    @Test
    public void shouldReturnNotFoundWhenRetrievingNonexistentComment() throws Exception {
        String errorMsg = "Comment not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();
        when(commentService.findById(anyLong()))
                .thenThrow(new EntityNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));
    }

    @Test
    public void shouldReturnOkWhenPatchingCommentSuccessfully() throws Exception {
        when(commentService.patchById(any(CommentRequest.class), anyLong(), any(Authentication.class)))
                .thenReturn(commentResponse);
        mockMvc.perform(MockMvcRequestBuilders.patch("/comments/{id}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(commentResponse)));

        verify(commentService, times(1))
                .patchById(any(CommentRequest.class), anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnNotFoundWhenPatchingNonexistentComment() throws Exception {
        String errorMsg = "Comment not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();
        when(commentService.patchById(any(CommentRequest.class), anyLong(), any(Authentication.class)))
                .thenThrow(new EntityNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.patch("/comments/{id}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(commentService, times(1))
                .patchById(any(CommentRequest.class), anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnBadRequestWhenPatchingOtherUserComment() throws Exception {
        String errorMsg = "Cannot update other users comment";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();
        when(commentService.patchById(any(CommentRequest.class), anyLong(), any(Authentication.class)))
                .thenThrow(new OperationNotPermittedException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.patch("/comments/{id}", 1L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(commentService, times(1))
                .patchById(any(CommentRequest.class), anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnOkWhenDeletingCommentSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteById(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistingComment() throws Exception {
        String errorMsg = "Comment not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        doThrow(new EntityNotFoundException(errorMsg))
                .when(commentService).deleteById(anyLong(), any(Authentication.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(commentService, times(1)).deleteById(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnBadRequestWhenDeletingOtherUserComment() throws Exception {
        String errorMsg = "Cannot delete other users comment";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();

        doThrow(new OperationNotPermittedException(errorMsg))
                .when(commentService).deleteById(anyLong(), any(Authentication.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(commentService, times(1)).deleteById(anyLong(), any(Authentication.class));
    }

    @Test
    public void shouldReturnOkWhenRetrievingAllCommentsForRecipeSuccessfully() throws Exception {
        List<CommentResponse> comments = List.of(commentResponse);
        PageResponse<CommentResponse> pageResponse = new PageResponse<>(
                comments,
                0,
                10,
                1,
                1,
                true,
                true);
        when(commentService.getAllCommentsForRecipe(anyLong(), anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/recipe/{recipe-id}", 1L)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(pageResponse)));

        verify(commentService, times(1)).getAllCommentsForRecipe(anyLong(), anyInt(), anyInt());
    }

    @Test
    public void shouldReturnNotFoundWhenRetrievingAllCommentsForNonExistingRecipe() throws Exception {
        String errorMsg = "Recipe not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();
        when(commentService.getAllCommentsForRecipe(anyLong(), anyInt(), anyInt()))
                .thenThrow(new EntityNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/recipe/{recipe-id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(commentService, times(1)).getAllCommentsForRecipe(anyLong(), anyInt(), anyInt());
    }

    @Test
    public void shouldReturnCreatedWhenAddingCommentSuccessfully() throws Exception {
        when(commentService.addComment(any(CommentRequest.class), anyLong())).thenReturn(commentResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments/recipe/{recipe-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "comments/" + comment.getCommentId()));

        verify(commentService, times(1)).addComment(any(CommentRequest.class), anyLong());
    }

    @Test
    public void shouldReturnNotFoundWhenAddingCommentOnNonexistentRecipe() throws Exception {
        String errorMsg = "Recipe not found";
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .error(errorMsg)
                .build();
        when(commentService.addComment(any(CommentRequest.class), anyLong()))
                .thenThrow(new EntityNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.post("/comments/recipe/{recipe-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(exceptionResponse)));

        verify(commentService, times(1)).addComment(any(CommentRequest.class), anyLong());
    }
}
