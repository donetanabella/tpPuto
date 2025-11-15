package backend.tpi.gestiontransportes.clients;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import backend.tpi.gestiontransportes.DTOS.TarifaDTO;

@Component
public class TarifasClient {

    private final RestClient restClient;

    public TarifasClient(@Value("${ms.solicitudes.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public TarifaDTO obtenerTarifaPorId(Integer id) {
        return this.restClient.get()
                    .uri("/tarifas/{id}", id) 
                    .retrieve()
                    .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (request, response) -> {
                    // Lanzar una excepción de Spring que se mapeará a un 404 si es necesario
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "la tarifa con ID " + id + " no fue encontrada.");
                    }).body(TarifaDTO.class);
    }

    public TarifaDTO obtenerTarifaParaVolumen(BigDecimal volumen) {
        
        // este tipo de dato es mejor para el manejo de listas
        ParameterizedTypeReference<List<TarifaDTO>> responseType = new ParameterizedTypeReference<List<TarifaDTO>>() {};

        // 2. Realizar la petición GET con el query parameter
        List<TarifaDTO> tarifasEncontradas = this.restClient.get()
            .uri("/tarifas?volumen={volumen}", volumen)
            .retrieve()
            // 3. Manejo de estados de error específicos, como 204 No Content
            .onStatus(status -> status.value() == HttpStatus.NO_CONTENT.value(), (req, res) -> {
                // Si el servicio responde 204, significa que no hay tarifas, lo tratamos como NOT_FOUND
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ninguna tarifa para el volumen: " + volumen);
            })
            .body(responseType); // Deserializa la respuesta a List<TarifaDTO>

        // 4. Verificar si la lista está vacía (puede devolver 200 OK con lista vacía)
        if (tarifasEncontradas == null || tarifasEncontradas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró ninguna tarifa válida para el volumen: " + volumen);
        }

        // 5. Retornar el primer elemento, como solicitaste.
        return tarifasEncontradas.get(0);
    }

}
