package backend.tpi.gestiondesolicitudes.config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;

/**
 * TokenRelayGatewayFilterFactory is responsible for relaying JWT tokens
 * from the API Gateway to downstream microservices.
 * 
 * This ensures that authenticated requests maintain their authorization context
 * when forwarded to other services in the microservices architecture.
 */
public class TokenRelayGatewayFilterFactory {
    
    /**
     * Extracts the JWT token from the security context
     * 
     * @return The Bearer token to be sent to downstream services
     */
    public String getTokenFromContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(jwt -> "Bearer " + jwt.getTokenValue())
                .orElse(null);
    }
    
    /**
     * Helper method to add the token to HTTP headers for microservice communication
     * 
     * @param token The token value
     * @param headers The HTTP headers to modify
     */
    public static void addTokenToHeaders(String token, HttpHeaders headers) {
        if (token != null && !token.isEmpty()) {
            headers.set(HttpHeaders.AUTHORIZATION, token);
        }
    }
}
