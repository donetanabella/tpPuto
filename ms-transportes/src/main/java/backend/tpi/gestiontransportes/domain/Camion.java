package backend.tpi.gestiontransportes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "camiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_camion")
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transportista", nullable = false)
    private Transportista transportista;

    @NotBlank
    @Column(name = "patente", nullable = false, unique = true, length = 20)
    private String patente;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "capacidadkg", precision = 12, scale = 2)
    private BigDecimal capacidadKg;

    @Column(name = "volumen_max", precision = 12, scale = 3)
    private BigDecimal volumenMax;

    @Column(name = "estado", length = 40)
    private String estado;

    @Column(name = "consumoxkm", precision = 10, scale = 3)
    private BigDecimal consumoXKm;

    @Column(name = "costo_base_trasladoxkm", precision = 12, scale = 2)
    private BigDecimal costoBaseTrasladoXKm;
}

