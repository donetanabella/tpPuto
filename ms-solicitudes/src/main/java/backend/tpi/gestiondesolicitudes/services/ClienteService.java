package backend.tpi.gestiondesolicitudes.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiondesolicitudes.domain.Cliente;
import backend.tpi.gestiondesolicitudes.repositorios.ClienteRepository;

@Service
public class ClienteService {
    
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.buscarPorId(id);
    }

    public Stream<Cliente> listarStream() {
        return clienteRepository.listarStream();
    }

    public Cliente guardar(Cliente nuevo) {
        
        return clienteRepository.guardar(nuevo);
    }

    public void eliminarPorId(Integer id) {
        clienteRepository.eliminarPorId(id);
    }

    public Optional<Cliente> modificar(Integer id, Cliente nuevo) {
        /*
         *   TODO: FALTA IMPLEMENTAR LOGICA DE MODIFICACION, EVITAR MODIFICAR ATRIBUTOS REPETIDOS
         */

        return clienteRepository.modificar(id, nuevo);
    }


}
