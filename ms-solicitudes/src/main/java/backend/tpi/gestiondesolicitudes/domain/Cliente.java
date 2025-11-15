package backend.tpi.gestiondesolicitudes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String telefono;

    private String email;

}
