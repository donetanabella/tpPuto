package backend.tpi.gestiontransportes.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.tpi.gestiontransportes.DTOS.SolicitudDestinoOrigenDTO;
import backend.tpi.gestiontransportes.clients.SolicitudesClient;
import backend.tpi.gestiontransportes.domain.Ruta;
import backend.tpi.gestiontransportes.domain.Tramo;
import backend.tpi.gestiontransportes.services.RutaService;
import backend.tpi.gestiontransportes.services.TramoService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("api/v1/rutas")
public class RutaController {

    private final RutaService rutaService;
    private final SolicitudesClient solicitudesClient;
    private final TramoService tramoService;

    public RutaController(RutaService rutaService, SolicitudesClient solicitudesClient, TramoService tramoService) {
        this.rutaService = rutaService;
        this.solicitudesClient = solicitudesClient;
        this.tramoService = tramoService;
    }

    // GET /api/v1/rutas
    @GetMapping
    public ResponseEntity<List<Ruta>> obtenerTodos() {
        List<Ruta> lista = rutaService.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/v1/rutas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerPorId(@PathVariable("id") Integer id) {
        Optional<Ruta> encontrado = rutaService.buscarPorId(id);
        return encontrado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/v1/rutas
    @PostMapping
    public ResponseEntity<Ruta> crear(@Valid @RequestBody Ruta nuevo) {
        Ruta guardado = rutaService.guardar(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/v1/rutas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizar(@PathVariable("id") Integer id,
                                           @Valid @RequestBody Ruta actualizado) {
        Optional<Ruta> res = rutaService.modificar(id, actualizado);
        return res.map(r -> ResponseEntity.status(HttpStatus.OK).body(r))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/v1/rutas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Integer id) {
        if (rutaService.existe(id)) {
            rutaService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/v1/rutas/{idSol}/rutas-posibles
    @GetMapping("/{idSol}/rutas-posibles")
    public ResponseEntity<List<Ruta>> obtenerRutasPosibles(@PathVariable("idSol") Integer idSol) {
        System.out.println("\n\n Testeando obtener rutas posibles \n\n");
        List<Ruta> listaRutasPosibles = rutaService.generarRutasPosibles(idSol);
        if (listaRutasPosibles.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(listaRutasPosibles);
    }

    // POST api/v1/rutas/{idSol}/asignar-ruta/{nroOrden}
    @GetMapping("{idSol}/asignar-ruta/{nroOrden}")  // capaz podria ser un POST pero lo hago GET porque no toma ningun body y es mas facil testearlo desde google
    public ResponseEntity<Ruta> asignarRuta(@PathVariable("idSol") Integer idSol, 
                                            @PathVariable("nroOrden") Integer nroOrden) {
        
        System.out.println("\n\nTesteando asignasion de rutas posibles\n\n");
        List<Ruta> listaRutasPosibles = rutaService.generarRutasPosibles(idSol);
        if (listaRutasPosibles.isEmpty()) return ResponseEntity.noContent().build();

        try {
            
            Ruta rutaSelecc = listaRutasPosibles.get(nroOrden);
            
            List<Tramo> tramos = rutaSelecc.getTramos();

            rutaService.guardar(rutaSelecc);
            
            for (Tramo tramo : tramos) {
                tramo.setRuta(rutaSelecc);
                // calcular costo aprox
                tramoService.guardar(tramo);
            }

            rutaSelecc.limpiarTramos();
            return ResponseEntity.ok(rutaSelecc);

        } 
        catch (IndexOutOfBoundsException e) {
            System.out.println("\n\n Ese número de orden no existe para esta solicitud");
            return ResponseEntity.badRequest().build();
        }

    }
    
}

