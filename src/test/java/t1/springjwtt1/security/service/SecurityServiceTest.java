package t1.springjwtt1.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import t1.springjwtt1.model.User;
import t1.springjwtt1.model.dto.AuthenticationRequestDto;
import t1.springjwtt1.model.dto.AuthenticationResponse;
import t1.springjwtt1.model.dto.RegisterRequest;
import t1.springjwtt1.model.enumeration.Role;
import t1.springjwtt1.model.enumeration.Status;
import t1.springjwtt1.repository.UserRepository;
import t1.springjwtt1.security.jwt.JwtTokenProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SecurityServiceTest {
    private static final String TOKEN = "$2a$12$qlYHSkJdo43zeveTlV5GiOjFo35hpBKWOwZnFFIM221RQqo5JV5Am";

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private RegisterRequest request;
    private AuthenticationRequestDto authenticationRequestDto;
    private AuthenticationResponse authenticationResponse;
    private User user;

    @InjectMocks
    SecurityService service;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        request = new RegisterRequest(
                "test@email.ru",
                "pass",
                "test",
                "test"
        );
        user = User.builder()
                .id(1L)
                .firstname("test")
                .lastname("test")
                .email("test@email.ru")
                .password("pass")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
        authenticationRequestDto = new AuthenticationRequestDto("test@email.ru", "pass");
        authenticationResponse = new AuthenticationResponse("test@email.ru", TOKEN);
    }

    @Test
    void authenticate_ShouldAuthenticationResponse() {
        when(userRepository.findByEmail(authenticationRequestDto.email())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(TOKEN);

        AuthenticationResponse actual = service.authenticate(authenticationRequestDto);

        assertEquals(authenticationResponse, actual);
        verify(userRepository, atLeast(1)).findByEmail(authenticationRequestDto.email());
    }

    @Test
    void register() {
        when(userRepository.save(user)).thenReturn(user);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(TOKEN);
        when(passwordEncoder.encode(anyString())).thenReturn("pass");

        AuthenticationResponse actual = service.register(request);

        assertEquals(authenticationResponse, actual);
    }
}