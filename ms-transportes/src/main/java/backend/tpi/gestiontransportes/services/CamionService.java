package backend.tpi.gestiontransportes.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiontransportes.domain.Camion;
import backend.tpi.gestiontransportes.repositorios.CamionRepository;

@Service
public class CamionService {

    private final CamionRepository camionRepository;

    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    public List<Camion> listarTodos() { return camionRepository.listarTodos(); }

    public Optional<Camion> buscarPorId(Integer id) { return camionRepository.buscarPorId(id); }

    public Stream<Camion> listarStream() { return camionRepository.listarStream(); }

    public Camion guardar(Camion nuevo) { return camionRepository.guardar(nuevo); }

    public void eliminarPorId(Integer id) { camionRepository.eliminarPorId(id); }

    public Optional<Camion> modificar(Integer id, Camion nuevo) { return camionRepository.modificar(id, nuevo); }

    public boolean existe(Integer id) { return camionRepository.existe(id); }
}

