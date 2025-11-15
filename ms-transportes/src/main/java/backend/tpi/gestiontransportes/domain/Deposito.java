package backend.tpi.gestiontransportes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "depositos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deposito")
    private Integer id;

    @NotBlank
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "direccion_txt", length = 200)
    private String direccionTxt;

    @Column(name = "longitud", precision = 9, scale = 6)
    private BigDecimal longitud;

    @Column(name = "latitud", precision = 9, scale = 6)
    private BigDecimal latitud;

    @Column(name = "costo_estadia_hora", precision = 12, scale = 2)
    private BigDecimal costoEstadiaHora;
}

