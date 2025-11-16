package backend.tpi.gestiontransportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Extract roles from realm_access claim
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                Object rolesObj = realmAccess.get("roles");
                if (rolesObj instanceof Collection<?>) {
                    Collection<?> rolesList = (Collection<?>) rolesObj;
                    rolesList.forEach(role -> {
                        if (role instanceof String) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + ((String) role).toUpperCase()));
                        }
                    });
                }
            }
            
            // Extract roles from resource_access claim if available
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                resourceAccess.forEach((key, value) -> {
                    if (value instanceof Map<?, ?>) {
                        Map<?, ?> resource = (Map<?, ?>) value;
                        Object rolesObj = resource.get("roles");
                        if (rolesObj instanceof Collection<?>) {
                            Collection<?> rolesList = (Collection<?>) rolesObj;
                            rolesList.forEach(role -> {
                                if (role instanceof String) {
                                    authorities.add(new SimpleGrantedAuthority("ROLE_" + ((String) role).toUpperCase()));
                                }
                            });
                        }
                    }
                });
            }
            
            return authorities;
        });

        return converter;
    }
}
