package backend.tpi.gestiontransportes.DTOS;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaDTO {
    private Integer id;
    private Integer idSolicitud;
    private Integer cantTramos;
    private Integer cantDepositos;
}
