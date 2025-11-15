package backend.tpi.gestiontransportes.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiontransportes.domain.Deposito;
import backend.tpi.gestiontransportes.repositorios.DepositoRepository;

@Service
public class DepositoService {

    private final DepositoRepository depositoRepository;

    public DepositoService(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    public List<Deposito> listarTodos() { return depositoRepository.listarTodos(); }

    public Optional<Deposito> buscarPorId(Integer id) { return depositoRepository.buscarPorId(id); }

    public Stream<Deposito> listarStream() { return depositoRepository.listarStream(); }

    public Deposito guardar(Deposito nuevo) { return depositoRepository.guardar(nuevo); }

    public void eliminarPorId(Integer id) { depositoRepository.eliminarPorId(id); }

    public Optional<Deposito> modificar(Integer id, Deposito nuevo) { return depositoRepository.modificar(id, nuevo); }

    public boolean existe(Integer id) { return depositoRepository.existe(id); }
}

