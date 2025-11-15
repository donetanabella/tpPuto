package backend.tpi.gestiondesolicitudes.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.tpi.gestiondesolicitudes.domain.Solicitud;

public interface SolicitudRepository extends BaseRepository<Solicitud, Integer> {

    @Query("SELECT s FROM Solicitud s WHERE s.contenedor.id = :contenedorId")
    Optional<Solicitud> buscarPorContenedor(@Param("contenedorId") Integer contenedorId);

}
