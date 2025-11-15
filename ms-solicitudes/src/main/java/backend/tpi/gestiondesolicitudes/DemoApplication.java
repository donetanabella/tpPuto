package backend.tpi.gestiondesolicitudes;

import java.math.BigDecimal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import backend.tpi.gestiondesolicitudes.domain.Cliente;
import backend.tpi.gestiondesolicitudes.domain.Tarifa;
import backend.tpi.gestiondesolicitudes.services.ClienteService;
import backend.tpi.gestiondesolicitudes.services.TarifaService;
import backend.tpi.gestiondesolicitudes.repositorios.ClienteRepository;
import backend.tpi.gestiondesolicitudes.repositorios.TarifaRepository;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		System.out.println("funcionando.......................\n\n\n");

		/*
		Cliente cliente = new Cliente();
		cliente.setNombre("Juan");
		cliente.setTelefono("123456789");
		cliente.setEmail("juan.perez@example.com");

		ClienteRepository clienteRepository = context.getBean(ClienteRepository.class);

		ClienteService clienteService = new ClienteService(clienteRepository);
		clienteService.guardar(cliente);
		clienteService.guardar(cliente);


		 * TarifaRepository tarifaRepository = context.getBean(TarifaRepository.class);
		 * TarifaService tarifaService = new TarifaService(tarifaRepository);
		 * 
		 * BigDecimal bd = new BigDecimal(1);
		 * BigDecimal bd2 = new BigDecimal(100);
		 * 
		 * Tarifa tarifa = new Tarifa();
		 * tarifa.setDescripcion("a");
		 * tarifa.setVolMin(bd);
		 * tarifa.setVolMax(bd2);
		 * tarifa.setCostoBaseKmXVol(bd2);
		 * tarifa.setValorCombustible(bd2);
		 * tarifa.setActiva(true);
		 * 
		 * tarifaService.guardar(tarifa);
		 */
	}

}
