package backend.tpi.gestiondesolicitudes.DTOS.solicitudes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteDTO {
    @NotBlank
    private String nombre;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String telefono;

}
