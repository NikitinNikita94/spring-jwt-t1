package t1.springjwtt1.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import t1.springjwtt1.exception.JwtAuthenticationException;
import t1.springjwtt1.model.User;
import t1.springjwtt1.model.dto.AuthenticationRequestDto;
import t1.springjwtt1.model.dto.AuthenticationResponse;
import t1.springjwtt1.model.dto.RegisterRequest;
import t1.springjwtt1.model.enumeration.Role;
import t1.springjwtt1.model.enumeration.Status;
import t1.springjwtt1.repository.UserRepository;
import t1.springjwtt1.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод аутентификации пользователя в приложении.
     *
     * @param request - входные данные пользователя.
     * @return - токен и email.
     */
    public AuthenticationResponse authenticate(final AuthenticationRequestDto request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
            String token = jwtTokenProvider.generateToken(request.email());

            return new AuthenticationResponse(user.getEmail(), token);
        } catch (AuthenticationException e) {
            throw new JwtAuthenticationException(e.getMessage());
        }
    }

    /**
     * Метод для регистрации нового пользователя в приложении.
     *
     * @param request - входные параметры пользователя.
     * @return - сохраненные данные.
     */
    public AuthenticationResponse register(final RegisterRequest request) {
        User user = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthenticationResponse(user.getEmail(), token);
    }
}
