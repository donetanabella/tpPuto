package backend.tpi.gestiontransportes.DTOS.geoApi;

import lombok.Data;

@Data
public class DistanciaDTO {
    private String origen;
    private String destino;
    private double kilometros;
    private String duracionTexto;
}
