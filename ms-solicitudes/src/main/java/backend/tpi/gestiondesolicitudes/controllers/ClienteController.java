package backend.tpi.gestiondesolicitudes.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.tpi.gestiondesolicitudes.domain.Cliente;
import backend.tpi.gestiondesolicitudes.services.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // GET /api/v1/clientes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Cliente>> obtenerTodosClientes() {
        
        List<Cliente> clientesEncontrados = clienteService.listarTodos();

        if (clientesEncontrados.isEmpty()) {
            // 204
            return ResponseEntity.noContent().build();
        }

        // 200
        return ResponseEntity.ok(clientesEncontrados);
    }

    // GET /api/v1/clientes/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable("id") Integer id) {
        // Verificar si el cliente existe
        Optional<Cliente> clienteEncontrado = clienteService.buscarPorId(id);
        if (clienteEncontrado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Validar ownership: si es CLIENTE, solo puede acceder a sus propios datos
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                // Es CLIENTE, verificar que sea dueño del registro
                String emailFromJwt = jwt.getClaimAsString("email");
                String emailCliente = clienteEncontrado.get().getEmail();

                if (emailFromJwt == null || !emailFromJwt.equals(emailCliente)) {
                    // No es el dueño, denegar acceso
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        return ResponseEntity.ok(clienteEncontrado.get());
    }
    
    // POST /api/v1/clientes
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente clienteNuevo) {
        Cliente clienteGuardado = clienteService.guardar(clienteNuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);
    }

    // PUT /api/v1/clientes/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable("id") Integer id, @RequestBody Cliente nuevoCliente) {
        // Verificar si el cliente existe
        Optional<Cliente> clienteExistente = clienteService.buscarPorId(id);
        if (clienteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Validar ownership: si es CLIENTE, solo puede actualizar sus propios datos
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                // Es CLIENTE, verificar que sea dueño del registro
                String emailFromJwt = jwt.getClaimAsString("email");
                String emailCliente = clienteExistente.get().getEmail();

                if (emailFromJwt == null || !emailFromJwt.equals(emailCliente)) {
                    // No es el dueño, denegar acceso
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        Optional<Cliente> clienteActualizado = clienteService.modificar(id, nuevoCliente);
        return clienteActualizado.map(c -> ResponseEntity.status(HttpStatus.OK).body(c))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE /api/v1/clientes/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCliente(@PathVariable("id") Integer id) {
        // Verificar existencia
        Optional<Cliente> existente = clienteService.buscarPorId(id);
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        clienteService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }


}
