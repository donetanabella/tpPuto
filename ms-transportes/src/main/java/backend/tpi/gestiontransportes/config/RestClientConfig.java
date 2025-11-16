package backend.tpi.gestiontransportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * RestClient Configuration for token relay to downstream microservices
 */
@Configuration
public class RestClientConfig {

    /**
     * RestClient bean with automatic JWT token relay
     * Adds the Authorization header with the current JWT token to all outgoing requests
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    // Extract JWT from security context and add to Authorization header
                    String token = getTokenFromContext();
                    if (token != null && !token.isEmpty()) {
                        request.getHeaders().set(HttpHeaders.AUTHORIZATION, token);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    /**
     * Extracts the JWT token from the security context
     *
     * @return The Bearer token or null if no token is found
     */
    public static String getTokenFromContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(jwt -> "Bearer " + jwt.getTokenValue())
                .orElse(null);
    }
}
