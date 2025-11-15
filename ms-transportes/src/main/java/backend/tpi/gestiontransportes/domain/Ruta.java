package backend.tpi.gestiontransportes.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer id;

    // Referencia a solicitudes (microservicio ms-solicitudes). La mantenemos como entero simple.
    @Column(name = "id_solicitud", nullable = false)
    private Integer idSolicitud;

    @Column(name = "cant_tramos")
    private Integer cantTramos;

    @Column(name = "cant_depositos")
    private Integer cantDepositos;

    // aunque son atributos calculados, simplifican calculos futuros
    @Column(name = "costo_aprox", precision = 14, scale = 2)
    private BigDecimal costoAprox;

    @Column(name = "costo_real", precision = 14, scale = 2)
    private BigDecimal costoReal;

    @Transient
    private List<Tramo> tramos;

    public void agregarTramo(Tramo tramo) {
        if (this.tramos == null) {
            this.tramos = new ArrayList<>();
        }
        this.tramos.add(tramo);
    }

    public void limpiarTramos() {
        this.tramos = new ArrayList<>();
    }
}

