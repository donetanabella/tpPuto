package backend.tpi.gestiontransportes.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import backend.tpi.gestiontransportes.domain.Camion;
import backend.tpi.gestiontransportes.services.CamionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/camiones")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    // GET /api/v1/camiones
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Camion>> obtenerTodos() {
        List<Camion> lista = camionService.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/v1/camiones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Camion> obtenerPorId(@PathVariable("id") Integer id) {
        Optional<Camion> encontrado = camionService.buscarPorId(id);
        return encontrado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/v1/camiones
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Camion> crear(@Valid @RequestBody Camion nuevo) {
        Camion guardado = camionService.guardar(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/v1/camiones/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Camion> actualizar(@PathVariable("id") Integer id,
                                             @Valid @RequestBody Camion actualizado) {
        Optional<Camion> res = camionService.modificar(id, actualizado);
        return res.map(c -> ResponseEntity.status(HttpStatus.OK).body(c))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/v1/camiones/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrar(@PathVariable("id") Integer id) {
        if (camionService.existe(id)) {
            camionService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

