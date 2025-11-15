package backend.tpi.gestiontransportes.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.tpi.gestiontransportes.domain.Camion;
import backend.tpi.gestiontransportes.domain.Ruta;
import backend.tpi.gestiontransportes.domain.Tramo;
import backend.tpi.gestiontransportes.services.CamionService;
import backend.tpi.gestiontransportes.services.RutaService;
import backend.tpi.gestiontransportes.services.TramoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/tramos")
public class TramoController {

    private final TramoService tramoService;
    private final CamionService camionService;
    private final RutaService rutaService;

    public TramoController(TramoService tramoService, CamionService camionService, RutaService rutaService) {
        this.tramoService = tramoService;
        this.camionService = camionService;
        this.rutaService = rutaService;
    }

    // GET /api/v1/tramos
    @GetMapping
    public ResponseEntity<List<Tramo>> obtenerTodos() {
        List<Tramo> lista = tramoService.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/v1/tramos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Tramo> obtenerPorId(@PathVariable("id") Integer id) {
        Optional<Tramo> encontrado = tramoService.buscarPorId(id);
        return encontrado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/v1/tramos
    @PostMapping
    public ResponseEntity<Tramo> crear(@Valid @RequestBody Tramo nuevo) {
        Tramo guardado = tramoService.guardar(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/v1/tramos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Tramo> actualizar(@PathVariable("id") Integer id,
                                            @Valid @RequestBody Tramo actualizado) {
        Optional<Tramo> res = tramoService.modificar(id, actualizado);
        return res.map(t -> ResponseEntity.status(HttpStatus.OK).body(t))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/v1/tramos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Integer id) {
        if (tramoService.existe(id)) {
            tramoService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("{idTramo}/asignar-camion/{idCamion}")
    public ResponseEntity<Tramo> asignarCamion(@PathVariable("idCamion") Integer idCamion, @PathVariable("idTramo") Integer idTramo) {
        
        Optional<Tramo> tramoOpt = tramoService.buscarPorId(idTramo);
        if (tramoOpt.isEmpty()) { return ResponseEntity.notFound().build(); }


        Optional<Camion> camionOpt = camionService.buscarPorId(idCamion);
        if (camionOpt.isEmpty()) { return ResponseEntity.notFound().build(); }

        Tramo tramoEncontrado = tramoOpt.get();
        Camion camionEncontrado = camionOpt.get();

        tramoEncontrado.setCamion(camionEncontrado);

        Optional<Tramo> tramoModificado = tramoService.modificar(idTramo, tramoEncontrado);

        return tramoModificado.map(t -> ResponseEntity.status(HttpStatus.OK).body(t))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("{idTramo}/inicio")
    public ResponseEntity<Tramo> asignarInicio(@PathVariable Integer idTramo) {

        Optional<Tramo> tramoOpt = tramoService.buscarPorId(idTramo);
        if (tramoOpt.isEmpty()) { return ResponseEntity.notFound().build(); }

        Tramo tramoEncontrado = tramoOpt.get();

        // si ya tiene fecha asignada tiramos error
        if (tramoEncontrado.getFechaHoraInicio() != null) {  return ResponseEntity.badRequest().build();}

        // si todavia no tiene camion asignado tiramos error (no se puede iniciar el tramo si no tiene un camión)
        if (tramoEncontrado.getCamion() == null) {return ResponseEntity.badRequest().build();}

        // buscar tramo anterior y verificar que haya finalizado, o que este tramo sea el primero del recorrido
        int nroOrdenActual = tramoEncontrado.getNroOrden();
        int idRuta = tramoEncontrado.getRuta().getId();
        List<Tramo> tramosRuta = tramoService.buscarPorRuta(idRuta);
        if (nroOrdenActual != 0 && tramosRuta.get(nroOrdenActual -1).getFechaHoraFin() == null) {
            return ResponseEntity.badRequest().build();
        }


        LocalDateTime fechaHoraInicio = LocalDateTime.now();

        tramoEncontrado.setFechaHoraInicio(fechaHoraInicio);
        tramoEncontrado.setEstado("comenzado");

        Optional<Tramo> tramoModificado = tramoService.modificar(idTramo, tramoEncontrado);

        return tramoModificado.map(t -> ResponseEntity.status(HttpStatus.OK).body(t))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("{idTramo}/fin")
    public ResponseEntity<Tramo> asignarFin(@PathVariable Integer idTramo) {

        Optional<Tramo> tramoOpt = tramoService.buscarPorId(idTramo);
        if (tramoOpt.isEmpty()) { return ResponseEntity.notFound().build(); }

        Tramo tramoEncontrado = tramoOpt.get();

        // si ya tiene fecha asignada tiramos error
        if (tramoEncontrado.getFechaHoraFin() != null) {  return ResponseEntity.badRequest().build();}

        // si todavia no tiene fecha de inicio tiramos error (no puede finalizar el tramo si nisiquiera inició)
        if (tramoEncontrado.getFechaHoraInicio() == null) { return ResponseEntity.badRequest().build(); }

        LocalDateTime fechaHoraFin = LocalDateTime.now();

        tramoEncontrado.setFechaHoraFin(fechaHoraFin);
        tramoEncontrado.setEstado("finalizado");

        Tramo tramoActualizado = tramoService.calcularCostoReal(tramoEncontrado);

        /*
         * ver si este es el ultimo tramo de la ruta, hay que calcular el costo real de la ruta, 
         * cambiarle el estado a la ruta y a la solicitud tambien creo
        */

        // si el tramo no tiene depositoDestino, es porque es el ultimo tramo de la ruta (o el unico, en su defecto)
        if (tramoEncontrado.getDepositoDestino() == null) {
            // es el ultimo
            Ruta rutaEncontrada = tramoEncontrado.getRuta();
            Ruta rutaFinalizada = rutaService.calcularCostoReal(rutaEncontrada);
            rutaService.modificar(rutaEncontrada.getId(), rutaFinalizada);
        }

        Optional<Tramo> tramoModificado = tramoService.modificar(idTramo, tramoActualizado);

        return tramoModificado.map(t -> ResponseEntity.status(HttpStatus.OK).body(t))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}

