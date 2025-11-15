package backend.tpi.gestiondesolicitudes.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contenedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Contenedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contenedor")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    private BigDecimal pesoKg;
    private BigDecimal volumen;
    private String estado;

}
