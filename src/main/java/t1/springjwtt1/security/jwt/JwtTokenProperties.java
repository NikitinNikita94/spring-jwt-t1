package t1.springjwtt1.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.jwt")
public record JwtTokenProperties(String authorizationHeader, String secret, Long jwtTokenValidityInMs) {
}
