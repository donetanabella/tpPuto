package backend.tpi.gestiontransportes.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenedorDTO {
    private Integer id;
    
    // Referencia al Cliente (importante para identificar a quién pertenece)
    private Integer idCliente; 

    private BigDecimal pesoKg;
    private BigDecimal volumen;
    private String estado;
}
