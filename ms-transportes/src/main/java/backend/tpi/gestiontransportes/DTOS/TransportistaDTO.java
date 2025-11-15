package backend.tpi.gestiontransportes.DTOS;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportistaDTO {
    private Integer id;
    private String nombre;
    private String telefono;
}
