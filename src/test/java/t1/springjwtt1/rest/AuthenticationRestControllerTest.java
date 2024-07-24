package t1.springjwtt1.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import t1.springjwtt1.model.dto.AuthenticationRequestDto;
import t1.springjwtt1.model.dto.AuthenticationResponse;
import t1.springjwtt1.model.dto.RegisterRequest;
import t1.springjwtt1.security.service.SecurityService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthenticationRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private RegisterRequest request;
    private AuthenticationRequestDto authenticationRequestDto;
    private AuthenticationResponse authenticationResponse;
    private String jsonReq;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        request = new RegisterRequest(
                "test@email.ru",
                "pass",
                "test",
                "test"
        );
        authenticationRequestDto = new AuthenticationRequestDto("test@email.ru", "pass");
        authenticationResponse = new AuthenticationResponse("test@email.ru", "$2a$12$qlYHSkJdo43zeveTlV5GiOjFo35hpBKWOwZnFFIM221RQqo5JV5Am");


        when(securityService.register(request)).thenReturn(authenticationResponse);
        when(securityService.authenticate(authenticationRequestDto)).thenReturn(authenticationResponse);
    }

    @Test
    void authenticate() throws Exception {
        jsonReq = objectMapper.writeValueAsString(authenticationRequestDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/login")
                        .content(jsonReq)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        System.out.println(contentAsString);

        assertNotNull(contentAsString);
        assertTrue(contentAsString.contains(authenticationResponse.email()));
        assertTrue(contentAsString.contains(authenticationResponse.token()));
    }

    @Test
    void register() throws Exception {
        jsonReq = objectMapper.writeValueAsString(request);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/register")
                        .content(jsonReq)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertNotNull(contentAsString);
        assertTrue(contentAsString.contains(authenticationResponse.email()));
        assertTrue(contentAsString.contains(authenticationResponse.token()));
    }
}