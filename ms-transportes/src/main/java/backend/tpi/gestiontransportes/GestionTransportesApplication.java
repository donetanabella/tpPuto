package backend.tpi.gestiontransportes;

import java.math.BigDecimal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import backend.tpi.gestiontransportes.domain.Transportista;
import backend.tpi.gestiontransportes.services.RutaService;
import backend.tpi.gestiontransportes.services.TransportistaService;

@SpringBootApplication
public class GestionTransportesApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(GestionTransportesApplication.class, args);
		System.out.println("funcionando..............\n\n\n");

		// // Obtenemos el service desde el contexto de Spring
		// TransportistaService service = context.getBean(TransportistaService.class);

		// // Creamos un transportista de prueba
		// Transportista nuevo = Transportista.builder()
		// .nombre("Juan Pérez")
		// .telefono("351-555-1234")
		// .build();

		// // Lo guardamos en la BD
		// Transportista guardado = service.guardar(nuevo);
		RutaService service = context.getBean(RutaService.class);
		BigDecimal consumoAprox = service.calcular_consumo_combustible_aprox();
		System.out.println("\n\nTESTING CONSUMO DE COMBUSTIBLE APROX: \n\n");
		System.out.println("==============: "+ consumoAprox);
	}

}
