package backend.tpi.gestiondesolicitudes.DTOS.solicitudes;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ContenedorDTO {
    @Min(1)
    private double pesoKg;
    @Min(1)
    private double volumenM3;

}
