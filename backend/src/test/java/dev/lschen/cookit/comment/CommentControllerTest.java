package dev.lschen.cookit.comment;

import dev.lschen.cookit.security.JwtFilter;
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
    public void getCommentsByIdEndpointTest() throws Exception {
        when(commentService.findById(anyLong())).thenReturn(commentResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(commentResponse)));
        verify(commentService, times(1)).findById(anyLong());
    }

    @Test
    public void patchCommentByIdEndpointTest() throws Exception {
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
    public void deleteCommentByIdEndpointTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteById(anyLong(), any(Authentication.class));
    }

    @Test
    public void getAllCommentsOfRecipeEndpointTest() throws Exception {
        when(commentService.getAllCommentsForRecipe(anyLong())).thenReturn(List.of(commentResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/recipe/{recipe-id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(List.of(commentResponse))));

        verify(commentService, times(1)).getAllCommentsForRecipe(anyLong());
    }

    @Test
    public void postCommentForRecipeEndpointTest() throws Exception {
        when(commentService.addComment(any(CommentRequest.class), anyLong())).thenReturn(commentResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/comments/recipe/{recipe-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "comments/" + comment.getCommentId()));

        verify(commentService, times(1)).addComment(any(CommentRequest.class), anyLong());
    }
}
