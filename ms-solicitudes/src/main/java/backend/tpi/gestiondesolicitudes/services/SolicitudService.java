package backend.tpi.gestiondesolicitudes.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiondesolicitudes.domain.Solicitud;
import backend.tpi.gestiondesolicitudes.repositorios.ClienteRepository;
import backend.tpi.gestiondesolicitudes.repositorios.ContenedorRepository;
import backend.tpi.gestiondesolicitudes.repositorios.SolicitudRepository;
import backend.tpi.gestiondesolicitudes.repositorios.TarifaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final TarifaRepository tarifaRepository;

    public List<Solicitud> listarTodos() {
        return solicitudRepository.listarTodos();
    }

    public Optional<Solicitud> buscarPorId(Integer id) {
        return solicitudRepository.buscarPorId(id);
    }

    public Stream<Solicitud> listarStream() {
        return solicitudRepository.listarStream();
    }

    @Transactional
    public Solicitud guardar(Solicitud nuevaSolicitud) {

        // verificar que la solicitud se cree en estado borrador
        if (nuevaSolicitud.getEstado() == null || !nuevaSolicitud.getEstado().equalsIgnoreCase("borrador")) {
            nuevaSolicitud.setEstado("borrador");
        }

        // la solicitud se tiene que crear sin ruta asociada. despues se le asignara

        // CLIENTE -- la solicitud tiene que registrarse con un cliente, puede existir o
        // no.
        if (nuevaSolicitud.getCliente() == null) {
            throw new IllegalArgumentException("Debe indicar un cliente en la solicitud.");
        }

        // verificar si el cliente existe sino crearlo
        var cliente = nuevaSolicitud.getCliente();

        if (cliente.getId() != null) {
            // si viene con id, verificamos que exista
            if (clienteRepository.existe(cliente.getId())) {
                cliente = clienteRepository.buscarPorId(cliente.getId()).get();
            } else {
                throw new IllegalArgumentException("El cliente indicado no existe en la base de datos.");
            }
        } else {
            // si no tiene id, lo creamos nuevo
            clienteRepository.guardar(cliente);
        }

        nuevaSolicitud.setCliente(cliente);

        // CONTENEDOR -- verificar si el contenedor existe o retorno null
        if (nuevaSolicitud.getContenedor() == null) {
            throw new IllegalArgumentException("Debe indicar un contenedor en la solicitud.");
        }

        // validar peso y volumen del contenedor
        var contenedor = nuevaSolicitud.getContenedor();
        if (contenedor.getPesoKg() == null || contenedor.getPesoKg().compareTo(BigDecimal.ZERO) <= 0
                || contenedor.getVolumen() == null || contenedor.getVolumen().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El contenedor debe tener un peso y volumen mayor a 0.");
        }

        contenedor.setCliente(cliente);
        contenedorRepository.guardar(contenedor);
        nuevaSolicitud.setContenedor(contenedor);

        // TARIFA -- verificar si la tarifa existe o retorno null
        if (nuevaSolicitud.getTarifa() == null || nuevaSolicitud.getTarifa().getId() == null) {
            throw new IllegalArgumentException("Debe indicar una tarifa válida.");
        }

        if (!tarifaRepository.existe(nuevaSolicitud.getTarifa().getId())) {
            throw new IllegalArgumentException("La tarifa indicada no existe.");
        }

        nuevaSolicitud.setEstado("borrador");
        nuevaSolicitud.setCostoEstimado(BigDecimal.ZERO);
        nuevaSolicitud.setCostoFinal(BigDecimal.ZERO);
        nuevaSolicitud.setTiempoEstimado(null);
        nuevaSolicitud.setTiempoFinal(null);

        // hay que tomar origen lat y long, y destino lat y long y mandar a crear una
        // ruta al ms-transporte con restclient
        // la creacion de una ruta desencadena la creacion de sus respectivos tramos
        // !! la ruta tiene una solicitud!! y los tramos tienen una ruta!!

        // una vez se haya creado la ruta asociada a la solicitud y sus respectivos
        // tramos:

        // calcular costo estimado -> sumar costo estimado de todos los tramos
        // asociasdos a la ruta
        // calcular tiempo estimado
        // calcular costo final
        // calcular tiempo final

        return solicitudRepository.guardar(nuevaSolicitud);
    }

    public void eliminarPorId(Integer id) {
        solicitudRepository.eliminarPorId(id);
    }

    public Optional<Solicitud> modificar(Integer id, Solicitud nuevo) {
        /*
         * TODO: FALTA IMPMENTAR LOGICA DE MODIFICACION PARA VER QUE NO SE CAMBIEN
         * ATRIBUTOS INVALIDOS O REPETIDOS
         */
        return solicitudRepository.modificar(id, nuevo);
    }

    public boolean existe(Integer id) {
        return solicitudRepository.existe(id);
    }

    public BigDecimal obtenerCostoAprox(Integer id) {
        return buscarPorId(id) // Optional<Solicitud>
                .map(Solicitud::getCostoEstimado) // si esta presente, obtenemos el costoEstimado
                .orElse(null); // si no esta presente, devolvemos 0 (o null, segun tu logica)
    }

    public BigDecimal obtenerCostoFinal(Integer id) {
        return buscarPorId(id) // Optional<Solicitud>
                .map(Solicitud::getCostoFinal) // si esta presente, obtenemos el costoEstimado
                .orElse(null); // si no esta presente, devolvemos 0 (o null, segun tu logica)
    }

}
