package backend.tpi.gestiondesolicitudes.DTOS;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositoDTO {
    private Integer id;
    private String nombre;
    private String direccionTxt;
    private BigDecimal longitud;
    private BigDecimal latitud;
    private BigDecimal costoEstadiaHora;
}