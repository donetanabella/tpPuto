package backend.tpi.gestiontransportes.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import backend.tpi.gestiontransportes.domain.Transportista;
import backend.tpi.gestiontransportes.services.TransportistaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/transportistas")
public class TransportistaController {

    private final TransportistaService transportistaService;

    public TransportistaController(TransportistaService transportistaService) {
        this.transportistaService = transportistaService;
    }

    // GET /api/v1/transportistas
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transportista>> obtenerTodos() {
        List<Transportista> lista = transportistaService.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/v1/transportistas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Transportista> obtenerPorId(@PathVariable("id") Integer id) {
        Optional<Transportista> encontrado = transportistaService.buscarPorId(id);
        return encontrado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/v1/transportistas
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transportista> crear(@Valid @RequestBody Transportista nuevo) {
        Transportista guardado = transportistaService.guardar(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/v1/transportistas/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transportista> actualizar(@PathVariable("id") Integer id,
                                                    @RequestBody Transportista actualizado) {
        Optional<Transportista> res = transportistaService.modificar(id, actualizado);
        return res.map(t -> ResponseEntity.status(HttpStatus.OK).body(t))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/v1/transportistas/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrar(@PathVariable("id") Integer id) {
        if (transportistaService.existe(id)) {
            transportistaService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
