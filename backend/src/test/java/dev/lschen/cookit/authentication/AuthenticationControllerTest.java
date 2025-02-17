package dev.lschen.cookit.authentication;

import dev.lschen.cookit.security.JwtFilter;
import dev.lschen.cookit.user.User;
import dev.lschen.cookit.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static dev.lschen.cookit.utils.TestUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    AuthenticationService authenticationService;

    @MockitoBean
    JwtFilter jwtService;

    @MockitoBean
    UserRepository userRepository;

    @Test
    public void registerEndpointTest() throws Exception {

        RegistrationRequest request = RegistrationRequest.builder()
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .email("email@mail.com")
                .password("password1")
                .build();

        doNothing().when(authenticationService).register(request);
        when(userRepository.save(any(User.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    public void activateAccountEndpointTest() throws Exception {

        String token = "test-token";
        doNothing().when(authenticationService).activateAccount(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/activate-account")
                        .param("token", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void loginEndpointTest() throws Exception {

        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("username")
                .password("password1")
                .build();

        AuthenticationResponse response = AuthenticationResponse
                .builder()
                .token("test-token")
                .build();

        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andReturn();
    }

}