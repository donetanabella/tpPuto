package backend.tpi.gestiondesolicitudes.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_contenedor", nullable = false)
    private Contenedor contenedor;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_tarifa", nullable = false)
    private Tarifa tarifa;

    // private BigDecimal costoEstimado;
    // private LocalDateTime tiempoEstimado;
    // private BigDecimal costoFinal;
    // private LocalDateTime tiempoFinal;
    // private String estado;

    // private String origenDireccion;
    // private Double origenLatitud;
    // private Double origenLongitud;

    // private String destinoDireccion;
    // private Double destinoLatitud;
    // private Double destinoLongitud;

    // private LocalDateTime fechaSolicitud;

    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "tiempo_estimado")
    private LocalDateTime tiempoEstimado;

    @Column(name = "costo_final")
    private BigDecimal costoFinal;

    @Column(name = "tiempo_final")
    private LocalDateTime tiempoFinal;

    @Column(name = "estado")
    private String estado;

    @Column(name = "origen_direccion")
    private String origenDireccion;

    @Column(name = "origen_latitud")
    private Double origenLatitud;

    @Column(name = "origen_longitud")
    private Double origenLongitud;

    @Column(name = "destino_direccion")
    private String destinoDireccion;

    @Column(name = "destino_latitud")
    private Double destinoLatitud;

    @Column(name = "destino_longitud")
    private Double destinoLongitud;

    @Column(name = "fechaSolicitud")
    private LocalDateTime fechaSolicitud;

}
