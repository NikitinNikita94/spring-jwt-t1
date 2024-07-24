package t1.springjwtt1.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import t1.springjwtt1.exception.JwtAuthenticationException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProperties jwtTokenProperties;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService detailsService,
                            JwtTokenProperties jwtTokenProperties) {
        this.userDetailsService = detailsService;
        this.jwtTokenProperties = jwtTokenProperties;
    }

    /**
     * Генерация нового токена.
     *
     * @param username - email пользователя
     * @return - токен
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Вспомогательный метод для генерации токена.
     *
     * @param username - email пользователя.
     * @return - токен
     */
    private String createToken(Map<String, Object> claims, final String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtTokenProperties.jwtTokenValidityInMs()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Метод по валидации и корректности токена.
     *
     * @param token - токен
     * @return - true or false
     */
    public boolean validateToken(final String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !claimsJws.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid", e);
        }
    }

    /**
     * Метод аутентифицирует токен в приложении.
     *
     * @param token - токен
     * @return - Authentication
     */
    public Authentication getAuthentication(final String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Метод вытаскивает имя пользователя из токена.
     *
     * @param token - токен
     * @return - имя пользователя
     */
    public String getUserName(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Метод вытаскивает токен из заголовков
     *
     * @param request - запрос
     * @return - токен
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(jwtTokenProperties.authorizationHeader());
    }

    /**
     * Метод генерирует секретный ключ.
     *
     * @return - секретный ключ
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtTokenProperties.secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
