package backend.tpi.gestiontransportes.DTOS;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositoDTO {
    private Integer id;
    private String nombre;
    private String direccionTxt;
    private BigDecimal longitud;
    private BigDecimal latitud;
    private BigDecimal costoEstadiaHora;
}
