package backend.tpi.gestiondesolicitudes.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiondesolicitudes.repositorios.TarifaRepository;
import backend.tpi.gestiondesolicitudes.domain.Tarifa;

@Service
public class TarifaService {
    
    private final TarifaRepository  tarifaRepository;

    public TarifaService(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    public List<Tarifa> listarTodos() {
        return tarifaRepository.listarTodos();
    }

    public Optional<Tarifa> buscarPorId(Integer id) {
        return tarifaRepository.buscarPorId(id);
    }

    public Stream<Tarifa> listarStream() {
        return tarifaRepository.listarStream();
    }

    public Tarifa guardar(Tarifa nuevo) {
        
        return tarifaRepository.guardar(nuevo);
    }

    public void eliminarPorId(Integer id) {
        tarifaRepository.eliminarPorId(id);
    }

    public Optional<Tarifa> modificar(Integer id, Tarifa nuevo) {
        /*
         *   TODO: FALTA IMPLEMENTAR LOGICA DE MODIFICACION, EVITAR MODIFICAR ATRIBUTOS REPETIDOS
         */
        return tarifaRepository.modificar(id, nuevo);
    }

    public boolean existe(Integer id) {
        return tarifaRepository.existe(id);
    }

    public List<Tarifa> buscarTarifasPorRangoVolumen(BigDecimal volumen) {
        // el mismo volumen se usa para buscar el límite inferior (volMin)
        // y el límite superior (volMax).
        return tarifaRepository.findByVolMinLessThanEqualAndVolMaxGreaterThan(volumen, volumen);
    }

}
