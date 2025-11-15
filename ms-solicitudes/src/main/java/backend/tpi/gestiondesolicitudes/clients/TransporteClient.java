package backend.tpi.gestiondesolicitudes.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TransporteClient {

    private final RestClient restClient;

    public TransporteClient(@Value("${ms.transporte.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    
}
