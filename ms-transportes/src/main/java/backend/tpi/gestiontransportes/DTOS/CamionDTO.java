package backend.tpi.gestiontransportes.DTOS;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CamionDTO {
    private Integer id;
    private Integer transportistaId;
    private String patente;
    private String telefono;
    private BigDecimal capacidadKg;
    private BigDecimal volumenMax;
    private String estado;
    private BigDecimal consumoXKm;
    private BigDecimal costoBaseTrasladoXKm;
}
