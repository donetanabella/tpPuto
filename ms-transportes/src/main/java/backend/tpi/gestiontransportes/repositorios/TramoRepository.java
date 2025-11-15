package backend.tpi.gestiontransportes.repositorios;

import java.util.List;

import backend.tpi.gestiontransportes.domain.Tramo;

public interface TramoRepository extends BaseRepository<Tramo, Integer> {
    

    List<Tramo> findByRuta_Id(Integer idRuta);
}

