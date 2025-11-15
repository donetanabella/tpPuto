package backend.tpi.gestiontransportes.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import backend.tpi.gestiontransportes.DTOS.ContenedorDTO;

@Component
public class ContenedoresClient {
    
    private final RestClient restClient;

    public ContenedoresClient(@Value("${ms.solicitudes.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ContenedorDTO obtenerContenedorPorId(Integer id) {
        return this.restClient.get()
                    .uri("/contenedores/{id}", id) 
                    .retrieve()
                    .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (request, response) -> {
                    // Lanzar una excepción de Spring que se mapeará a un 404 si es necesario
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "el contenedor con ID " + id + " no fue encontrado.");
                }).body(ContenedorDTO.class);
    }
}
