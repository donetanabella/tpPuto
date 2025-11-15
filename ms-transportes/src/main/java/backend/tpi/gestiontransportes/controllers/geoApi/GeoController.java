package backend.tpi.gestiontransportes.controllers.geoApi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import backend.tpi.gestiontransportes.DTOS.geoApi.DistanciaDTO;
import backend.tpi.gestiontransportes.services.geoApi.GeoService;

@RestController
@RequestMapping("/api/distancia")
@RequiredArgsConstructor

public class GeoController {
    private final GeoService geoService;

    @GetMapping
    public DistanciaDTO obtenerDistancia(
            @RequestParam String origen,
            @RequestParam String destino) throws Exception {
        return geoService.calcularDistancia(origen, destino);
    }

}