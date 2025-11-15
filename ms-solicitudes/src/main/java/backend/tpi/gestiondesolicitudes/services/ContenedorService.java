package backend.tpi.gestiondesolicitudes.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiondesolicitudes.domain.Contenedor;
import backend.tpi.gestiondesolicitudes.repositorios.ClienteRepository;
import backend.tpi.gestiondesolicitudes.repositorios.ContenedorRepository;

@Service
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;
    private final ClienteRepository clienteRepository;

    public ContenedorService(ContenedorRepository contenedorRepository, ClienteRepository clienteRepository) {
        this.contenedorRepository = contenedorRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Contenedor> listarTodos() {
        return contenedorRepository.listarTodos();
    }

    public Optional<Contenedor> buscarPorId(Integer id) {
        return contenedorRepository.buscarPorId(id);
    }

    public Stream<Contenedor> listarStream() {
        return contenedorRepository.listarStream();
    }

    public Optional<Contenedor> guardar(Contenedor nuevo) {

        // si se pasa el atributo de cliente vacio o cliente sin id, devolvemos optional
        // empty
        if (nuevo.getCliente() == null || nuevo.getCliente().getId() == null) {
            return Optional.empty();
        }

        // sino, lo buscamos al cliente por id. Si se encuentra devolvemos al cliente,
        // sino optional empty
        return clienteRepository.buscarPorId(nuevo.getCliente().getId())
                .map(cliente -> contenedorRepository.guardar(nuevo));
    }

    public void eliminarPorId(Integer id) {
        contenedorRepository.eliminarPorId(id);
    }

    public Optional<Contenedor> modificar(Integer id, Contenedor nuevo) {
        /*
         * TODO: FALTA IMPLEMENTAR LOGICA DE MODIFICACION, EVITAR MODIFICAR ATRIBUTOS
         * REPETIDOS
         */
        return contenedorRepository.modificar(id, nuevo);
    }

    public boolean existe(Integer id) {
        return contenedorRepository.existe(id);
    }

}
