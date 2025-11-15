package backend.tpi.gestiontransportes.services.geoApi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import backend.tpi.gestiontransportes.DTOS.geoApi.DistanciaDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class GeoService {
    @Value("${google.maps.apikey}")
    private String apiKey;
    private final RestClient.Builder builder;

    public DistanciaDTO calcularDistancia(String origen, String destino) throws Exception {
        RestClient client = builder.baseUrl("https://maps.googleapis.com/maps/api").build();

        String url = "/distancematrix/json?origins=" + origen +
                "&destinations=" + destino +
                "&units=metric&key=" + apiKey;

        ResponseEntity<String> response = client.get().uri(url).retrieve().toEntity(String.class);

        // Mostrar respuesta completa para depurar
        System.out.println("📦 Respuesta de Google API:");
        //System.out.println(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        // Validar status general
        String status = root.path("status").asText();
        if (!"OK".equals(status)) {
            throw new RuntimeException(
                    "Error en respuesta de Google API: " + status + " → " + response.getBody());
        }

        JsonNode rows = root.path("rows");
        if (rows.isMissingNode() || !rows.isArray() || rows.isEmpty()) {
            throw new RuntimeException("Respuesta inesperada: no se encontró 'rows' en el JSON: "
                    + response.getBody());
        }

        JsonNode elements = rows.get(0).path("elements");
        if (elements.isMissingNode() || !elements.isArray() || elements.isEmpty()) {
            throw new RuntimeException("Respuesta inesperada: no se encontró 'elements' en el JSON: "
                    + response.getBody());
        }

        JsonNode leg = elements.get(0);

        // Validar campos
        if (leg.path("status").asText().equals("ZERO_RESULTS")) {
            throw new RuntimeException("No se encontró ruta entre los puntos dados.");
        }

        DistanciaDTO dto = new DistanciaDTO();
        dto.setOrigen(origen);
        dto.setDestino(destino);
        dto.setKilometros(leg.path("distance").path("value").asDouble() / 1000);
        dto.setDuracionTexto(leg.path("duration").path("text").asText());

        return dto;
    }

}
