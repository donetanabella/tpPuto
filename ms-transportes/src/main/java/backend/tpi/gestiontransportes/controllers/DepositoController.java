package backend.tpi.gestiontransportes.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import backend.tpi.gestiontransportes.domain.Deposito;
import backend.tpi.gestiontransportes.services.DepositoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/depositos")
public class DepositoController {

    private final DepositoService depositoService;

    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    // GET /api/v1/depositos
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Deposito>> obtenerTodos() {
        List<Deposito> lista = depositoService.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/v1/depositos/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Deposito> obtenerPorId(@PathVariable("id") Integer id) {
        Optional<Deposito> encontrado = depositoService.buscarPorId(id);
        return encontrado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/v1/depositos
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Deposito> crear(@Valid @RequestBody Deposito nuevo) {
        Deposito guardado = depositoService.guardar(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/v1/depositos/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Deposito> actualizar(@PathVariable("id") Integer id,
                                               @Valid @RequestBody Deposito actualizado) {
        Optional<Deposito> res = depositoService.modificar(id, actualizado);
        return res.map(d -> ResponseEntity.status(HttpStatus.OK).body(d))
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/v1/depositos/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrar(@PathVariable("id") Integer id) {
        if (depositoService.existe(id)) {
            depositoService.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

