package backend.tpi.gestiondesolicitudes.DTOS;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TramoDTO {
    private Integer id;
    private Integer idCamion;
    private Integer idRuta;
    private Integer nroOrden;
    private Integer idDepositoOrigen;
    private Integer idDepositoDestino;
    private String tipoTramo;
    private String estado;
    private BigDecimal costoAprox;
    private BigDecimal costoReal;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
}