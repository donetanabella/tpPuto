package backend.tpi.gestiontransportes.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_camion", nullable = true)
    private Camion camion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    @Column(name = "nro_orden", nullable = false)
    private Integer nroOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_origen", nullable = true)
    private Deposito depositoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_destino", nullable = true)
    private Deposito depositoDestino;

    @Column(name = "tipo_tramo", length = 40)
    private String tipoTramo;

    @Column(name = "estado", length = 40)
    private String estado;

    @Column(name = "costo_aprox", precision = 14, scale = 2)
    private BigDecimal costoAprox;

    @Column(name = "costo_real", precision = 14, scale = 2)
    private BigDecimal costoReal;

    @Column(name = "fechaHora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fechaHora_fin")
    private LocalDateTime fechaHoraFin;

    // cambio para calcular mejor los costos
    @Column(name = "distancia_km")
    private BigDecimal distanciaKm;
}

