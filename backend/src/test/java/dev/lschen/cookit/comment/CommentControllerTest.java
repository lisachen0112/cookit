package dev.lschen.cookit.comment;

import dev.lschen.cookit.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    JwtFilter jwtService;
//
//    @BeforeEach
//    void setUp() {
//        Comment comment = new Comment();
//    }

    @Test
    public void getCommentsForRecipeEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/recipes/comments/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void postCommentForRecipeEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/recipes/comments/{id}", 1L))
                .andExpect(status().isCreated());
    }

    @Test
    public void patchCommentForRecipeEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/recipes/comments/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCommentForRecipeEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/recipes/comments/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
