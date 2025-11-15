package backend.tpi.gestiondesolicitudes.DTOS;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CamionDTO {
    private Integer id;
    private Integer idTransportista; // solo traemos la id, no el transportista
    private String patente;
    private String telefono;
    private BigDecimal capacidadKg;
    private BigDecimal volumenMax;
    private String estado;
    private BigDecimal consumoXKm;
    private BigDecimal costoBaseTrasladoXKm;
}