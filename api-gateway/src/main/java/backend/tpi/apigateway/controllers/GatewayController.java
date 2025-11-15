package backend.tpi.apigateway.controllers;

import backend.tpi.apigateway.services.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GatewayController {

    private final ProxyService proxyService;

    @RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })

    public ResponseEntity<String> proxyAll(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader Map<String, String> headers) {
        String method = request.getMethod();
        // request.getRequestURI() no incluye query string, así que la añadimos si
        // existe
        String fullPath = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : ""); // ejemplo:
                                                                                            // /api/v1/clientes?foo=1
        String targetPath = fullPath.replace("/api/v1", ""); // queda /clientes?foo=1
        return proxyService.forward(method, targetPath, body, headers);
    }
}
