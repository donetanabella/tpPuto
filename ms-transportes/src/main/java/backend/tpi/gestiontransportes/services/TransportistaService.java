package backend.tpi.gestiontransportes.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiontransportes.domain.Transportista;
import backend.tpi.gestiontransportes.repositorios.TransportistaRepository;

@Service
public class TransportistaService {

    private final TransportistaRepository transportistaRepository;

    public TransportistaService(TransportistaRepository transportistaRepository) {
        this.transportistaRepository = transportistaRepository;
    }

    public List<Transportista> listarTodos() { return transportistaRepository.listarTodos(); }

    public Optional<Transportista> buscarPorId(Integer id) { return transportistaRepository.buscarPorId(id); }

    public Stream<Transportista> listarStream() { return transportistaRepository.listarStream(); }

    public Transportista guardar(Transportista nuevo) { return transportistaRepository.guardar(nuevo); }

    public void eliminarPorId(Integer id) { transportistaRepository.eliminarPorId(id); }

    public Optional<Transportista> modificar(Integer id, Transportista nuevo) {
        return transportistaRepository.modificar(id, nuevo);
    }

    public boolean existe(Integer id) { return transportistaRepository.existe(id); }
}

