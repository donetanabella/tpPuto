package backend.tpi.gestiontransportes.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDTO {
    // Identificador de la Tarifa
    private Integer id;
    
    private String descripcion;
    
    // Rango de volumen (BigDecimal para alta precisión)
    private BigDecimal volMin;
    private BigDecimal volMax;

    // Costos
    private BigDecimal costoBaseKmXVol;
    private BigDecimal valorCombustible;

    // Estado
    private Boolean activa;
}
