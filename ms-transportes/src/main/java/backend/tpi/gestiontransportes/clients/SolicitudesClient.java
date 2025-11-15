package backend.tpi.gestiontransportes.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import backend.tpi.gestiontransportes.DTOS.SolicitudDestinoOrigenDTO;

@Component
public class SolicitudesClient {

    private final RestClient restClient;

    public SolicitudesClient(@Value("${ms.solicitudes.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public SolicitudDestinoOrigenDTO obtenerSolicitudPorId(Integer id) {
        
        return this.restClient.get()
                // La URI es solo /{id} porque el ID se pasa como variable de ruta en el controller
                .uri("/solicitudes/{id}", id) 
                .retrieve()
                // Manejo de error 404 explícito: si no se encuentra la solicitud
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (request, response) -> {
                    // Lanzar una excepción de Spring que se mapeará a un 404 si es necesario
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La solicitud con ID " + id + " no fue encontrada.");
                })
                // Mapear la respuesta JSON al DTO
                .body(SolicitudDestinoOrigenDTO.class);
    }

}
