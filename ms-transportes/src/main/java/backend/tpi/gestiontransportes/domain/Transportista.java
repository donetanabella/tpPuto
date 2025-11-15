package backend.tpi.gestiontransportes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "transportistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transportista")
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "telefono", length = 20)
    private String telefono;
}

