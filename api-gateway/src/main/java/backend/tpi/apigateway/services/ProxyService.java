package backend.tpi.apigateway.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final RestClient.Builder restClientBuilder;

    @Value("${ms.solicitudes.url}")
    private String baseUrlMsSolicitudes;

    @Value("${ms.transportes.url}")
    private String baseUrlMsTransportes;

    public ResponseEntity<String> forward(String method, String path, String body, Map<String, String> headers) {
        RestClient client = restClientBuilder.build();
        String targetUrl;
        if (path.startsWith("/solicitudes") || path.startsWith("/clientes") ||
                path.startsWith("/contenedores") || path.startsWith("/tarifas")) {
            targetUrl = baseUrlMsSolicitudes + path;
        } else if (path.startsWith("/transportistas") || path.startsWith("/camiones") ||
                path.startsWith("/depositos") || path.startsWith("/rutas") ||
                path.startsWith("/tramos")) {
            targetUrl = baseUrlMsTransportes + path;
        } else {
            // Por defecto, mandamos a ms-solicitudes
            targetUrl = baseUrlMsSolicitudes + path;
        }

        log.info("➡️ Redirigiendo [{}] a {}", method, targetUrl);

        var request = client.method(HttpMethod.valueOf(method))
                .uri(targetUrl);

        if (body != null && !body.isEmpty()) {
            request.body(body);
        }

    try {
        return request
            .retrieve()
            .toEntity(String.class);
        } catch (org.springframework.web.client.RestClientResponseException e) {
            // Si el downstream responde 4xx/5xx, devolvemos el mismo status y cuerpo al cliente
            String responseBody = e.getResponseBodyAsString();
            // En Spring 6+ RestClientResponseException expone getStatusCode() que devuelve
            // HttpStatusCode; no existe getRawStatusCode() en algunas versiones. Usamos
            // el valor numérico y lo pasamos directamente al ResponseEntity.
            int rawStatus = 500;
            try {
                if (e.getStatusCode() != null) {
                    rawStatus = e.getStatusCode().value();
                }
            } catch (Exception ex) {
                // fallback a 500 si no podemos obtener el status
                rawStatus = 500;
            }

            return ResponseEntity.status(rawStatus).body(responseBody);
        }
    }
}
