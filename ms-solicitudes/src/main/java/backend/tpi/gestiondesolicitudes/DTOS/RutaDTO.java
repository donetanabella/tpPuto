package backend.tpi.gestiondesolicitudes.DTOS;

import lombok.Data;

@Data
public class RutaDTO {
    private Integer id;
    private Integer idSolicitud;
    private Integer cantTramos;
    private Integer cantDepositos;
}