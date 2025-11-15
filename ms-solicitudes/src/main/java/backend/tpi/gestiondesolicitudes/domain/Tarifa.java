package backend.tpi.gestiondesolicitudes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Integer id;

    @NotBlank(message = "la descrip de tarifa es obligatoria")
    private String descripcion;
    
    @NotNull(message = "El volumen mínimo es obligatorio")
    private BigDecimal volMin;

    @NotNull(message = "El volumen máximo es obligatorio")
    private BigDecimal volMax;

    @NotNull(message = "El costo base por km x volumen es obligatorio")
    @Column(name = "costo_base_km_xvol")
    private BigDecimal costoBaseKmXVol;

    @NotNull(message = "El valor del combustible es obligatorio")
    private BigDecimal valorCombustible;

    @NotNull
    private Boolean activa;
}
