package backend.tpi.gestiontransportes.DTOS;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramoDTO {
    private Integer id;
    private Integer camionId;
    private Integer rutaId;
    private Integer nroOrden;
    private Integer depositoOrigenId;
    private Integer depositoDestinoId;
    private String tipoTramo;
    private String estado;
    private BigDecimal costoAprox;
    private BigDecimal costoReal;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
}
