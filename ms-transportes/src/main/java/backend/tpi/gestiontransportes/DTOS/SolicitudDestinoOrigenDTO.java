package backend.tpi.gestiontransportes.DTOS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para transferir solo los datos de ubicación (origen y destino) 
 * de una Solicitud entre microservicios.
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDestinoOrigenDTO {
    private String origenDireccion;
    private Double origenLatitud;
    private Double origenLongitud;

    private String destinoDireccion;
    private Double destinoLatitud;
    private Double destinoLongitud;

    // Identificador del Contenedor (referencia)
    private Integer idContenedor;

    // Campo auxiliar para capturar el objeto "contenedor" anidado
    @JsonProperty("contenedor")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private void unpackContenedor(java.util.Map<String, Object> contenedor) {
        if (contenedor != null && contenedor.get("id") != null) {
            this.idContenedor = (Integer) contenedor.get("id");
        }
    }
}
