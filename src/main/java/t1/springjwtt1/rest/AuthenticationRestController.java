package t1.springjwtt1.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import t1.springjwtt1.model.dto.AuthenticationRequestDto;
import t1.springjwtt1.model.dto.AuthenticationResponse;
import t1.springjwtt1.model.dto.RegisterRequest;
import t1.springjwtt1.security.service.SecurityService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Аутентификация и регистрация пользователя", description = "Эндпоинты для работы с аутентификацией и регистрацией")
public class AuthenticationRestController {

    private final SecurityService service;

    @PostMapping("/login")
    @Operation(
            summary = "Пройти аутентификацию",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticationResponse.class)
                            )
                    )
            }
    )
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequestDto authenticationRestDto) {
        return service.authenticate(authenticationRestDto);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Пройти регистрацию",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Запрос выполнен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticationResponse.class)
                            )
                    )
            }
    )
    public AuthenticationResponse register(@RequestBody RegisterRequest request) {
        System.out.println();
        return service.register(request);
    }
}
