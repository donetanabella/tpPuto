package backend.tpi.gestiontransportes.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import backend.tpi.gestiontransportes.domain.Camion;
import backend.tpi.gestiontransportes.domain.Deposito;
import backend.tpi.gestiontransportes.domain.Ruta;
import backend.tpi.gestiontransportes.domain.Tramo;
import backend.tpi.gestiontransportes.repositorios.RutaRepository;
import backend.tpi.gestiontransportes.services.geoApi.GeoService;
import backend.tpi.gestiontransportes.DTOS.ContenedorDTO;
import backend.tpi.gestiontransportes.DTOS.SolicitudDestinoOrigenDTO;
import backend.tpi.gestiontransportes.DTOS.TarifaDTO;
import backend.tpi.gestiontransportes.DTOS.geoApi.DistanciaDTO;
import backend.tpi.gestiontransportes.clients.ContenedoresClient;
import backend.tpi.gestiontransportes.clients.SolicitudesClient;
import backend.tpi.gestiontransportes.clients.TarifasClient;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final DepositoService depositoService;
    private final SolicitudesClient solicitudesClient;
    private final GeoService geoService;
    private final ContenedoresClient contenedoresClient;
    private final TarifasClient tarifasClient;
    private final CamionService camionService;
    private final TramoService tramoService;

    public RutaService(RutaRepository rutaRepository, 
                        SolicitudesClient solicitudesClient, 
                        GeoService geoService, 
                        DepositoService depositoService, 
                        ContenedoresClient contenedoresClient,
                        TarifasClient tarifasClient,
                        CamionService camionService,
                        TramoService tramoService) {
        this.rutaRepository = rutaRepository;
        this.solicitudesClient = solicitudesClient;
        this.geoService = geoService;
        this.depositoService = depositoService;
        this.contenedoresClient = contenedoresClient;
        this.tarifasClient = tarifasClient;
        this.camionService = camionService;
        this.tramoService = tramoService;
    }

    public List<Ruta> listarTodos() { return rutaRepository.listarTodos(); }

    public Optional<Ruta> buscarPorId(Integer id) { return rutaRepository.buscarPorId(id); }

    public Stream<Ruta> listarStream() { return rutaRepository.listarStream(); }

    public Ruta guardar(Ruta nuevo) { return rutaRepository.guardar(nuevo); }

    public void eliminarPorId(Integer id) { rutaRepository.eliminarPorId(id); }

    public Optional<Ruta> modificar(Integer id, Ruta nuevo) { return rutaRepository.modificar(id, nuevo); }

    public boolean existe(Integer id) { return rutaRepository.existe(id); }

    public List<Ruta> generarRutasPosibles(Integer idSolicitud) {

        // traer la solicitud
        SolicitudDestinoOrigenDTO solicitudDTO = this.solicitudesClient.obtenerSolicitudPorId(idSolicitud);

        // obtener el contenedor asociado a la solicitud
        Integer idContenedor = solicitudDTO.getIdContenedor();
        System.out.println("=============="+solicitudDTO);
        ContenedorDTO contenedorDTO = this.contenedoresClient.obtenerContenedorPorId(idContenedor);

        // obtener el volumen del contenedor para traer la tarifa correspondiente
        BigDecimal volumenContenedorEncontrado = contenedorDTO.getVolumen();

        // obtener la tarifa adecuada para el volumen de mi contenedor
        TarifaDTO tarifaDTO = this.tarifasClient.obtenerTarifaParaVolumen(volumenContenedorEncontrado);

        // obtenemos el origen y destino de la solicitd
        String origenDireccion = solicitudDTO.getOrigenDireccion();
        String destinoDireccion = solicitudDTO.getDestinoDireccion();

        List<Ruta> listaRutas = new ArrayList<>();

        try {

            // calculamos la duracion en hs y la distancia del recorrido directo
            DistanciaDTO distanciaDTO = geoService.calcularDistancia(origenDireccion, destinoDireccion);
            String duracionString = distanciaDTO.getDuracionTexto();
            double distanciaDestinoOrigen = distanciaDTO.getKilometros();
            int duracionInt = transformarAHoras(duracionString);

            if (duracionInt < 6) {
                // generamos un tramo unico
                Ruta rutaUnica = calcular_ruta_unica(idSolicitud, tarifaDTO, distanciaDestinoOrigen);
                listaRutas.add(rutaUnica);
            }

            if (duracionInt >= 6 && duracionInt <= 12) {
                // generar ruta de tramo unico y ruta de tramo doble
                Ruta rutaUnica = calcular_ruta_unica(idSolicitud, tarifaDTO, distanciaDestinoOrigen);
                Ruta rutaDoble = calcular_ruta_doble(idSolicitud, origenDireccion, destinoDireccion, tarifaDTO);
                listaRutas.add(rutaUnica);
                listaRutas.add(rutaDoble);
            }

            if (duracionInt > 12) {
                // generar ruta de tramo unico, doble y triple
                Ruta rutaUnica = calcular_ruta_unica(idSolicitud, tarifaDTO, distanciaDestinoOrigen);
                Ruta rutaDoble = calcular_ruta_doble(idSolicitud, origenDireccion, destinoDireccion, tarifaDTO);
                Ruta rutaTriple = calcular_ruta_triple(idSolicitud, origenDireccion, destinoDireccion, tarifaDTO);
                listaRutas.add(rutaUnica);
                listaRutas.add(rutaDoble);
                listaRutas.add(rutaTriple);
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n\n\nalgo falló desde RutaService - generarRutasPosibles()");
        }

        return listaRutas;
    }
    // public boolean asignarRutas() {}

    private Deposito obtenerDepositoMasCercano(String origen) {

        List<Deposito> depositos = depositoService.listarTodos();

        if (depositos == null || depositos.isEmpty()) {
            return null; // No hay depósitos para comparar
        }

        System.out.println("=======debuggin obtener depo mas cercano====");


        Optional<Deposito> resultado = depositos.stream().min(Comparator.comparingInt(deposito -> {
            String destinoDireccion = deposito.getDireccionTxt();
            try {
                DistanciaDTO distanciaDTO = geoService.calcularDistancia(origen, destinoDireccion);
                String duracionString = distanciaDTO.getDuracionTexto();
                System.out.println("\n\n\n\n=========");
                System.out.println("distancia hasta depo: "+deposito.getNombre()+" : "+duracionString);
                
                return transformarAHoras(duracionString);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("\n\n\nalgo fallo en rutaService - obtenerDepositoMasCercano()");
                return Integer.MAX_VALUE;
            }
        }));

        return resultado.orElse(null);
    }


    private Ruta calcular_ruta_unica(Integer idSolicitud, TarifaDTO tarifa, double distanciaTotal) {
        Ruta rutaUnica = new Ruta();
        Tramo tramo1 = new Tramo();

        //setear ruta
        rutaUnica.setIdSolicitud(idSolicitud);
        rutaUnica.setCantTramos(1);
        rutaUnica.setCantDepositos(0);
        
        //setear tramo (sin camion, con ruta, con nroOrden, sin depoDestino ni origen, calcular costo aprox)
        //tramo1.setRuta(rutaUnica);
        tramo1.setNroOrden(0);
        tramo1.setTipoTramo("origen-destino");
        tramo1.setEstado("no comenzado");

        // setear distancia en bigDecimal
        BigDecimal distanciaBigDecimal = new BigDecimal(String.valueOf(distanciaTotal));
        tramo1.setDistanciaKm(distanciaBigDecimal);
        
        // calcular costo aprox BigDecimal
        BigDecimal costoAproxTramo1 = calcular_costo_aprox_tramo(distanciaTotal, tarifa);
        tramo1.setCostoAprox(costoAproxTramo1);

        // agregar tramo a ruta
        rutaUnica.agregarTramo(tramo1);
        // la ruta unica tiene el mismo costo aprox que su unico tramo
        rutaUnica.setCostoAprox(costoAproxTramo1);

        return rutaUnica;
    }

    private Ruta calcular_ruta_doble(Integer idSolicitud, String origenDireccion, String destinoDireccion, TarifaDTO tarifa) {

        Deposito depositoIntermedio = obtenerDepositoMasCercano(origenDireccion);

        if (depositoIntermedio == null) {return null;}

        Ruta rutaDoble = new Ruta();
        Tramo tramo1 = new Tramo();
        Tramo tramo2 = new Tramo();

        // setear una ruta
        rutaDoble.setIdSolicitud(idSolicitud);
        rutaDoble.setCantTramos(2);
        rutaDoble.setCantDepositos(1);

        //setear tramo1
        //tramo1.setRuta(rutaDoble);
        tramo1.setNroOrden(0);
        tramo1.setTipoTramo("origen-deposito");
        tramo1.setDepositoDestino(depositoIntermedio);
        tramo1.setEstado("no comenzado");
        // calcular distancia para setearla luego:
        try {
            DistanciaDTO origenADepositoDTO = this.geoService.calcularDistancia(depositoIntermedio.getDireccionTxt(), origenDireccion);
            double distanciaOrigenDeposito = origenADepositoDTO.getKilometros();
            BigDecimal distancia1BigDecimal = new BigDecimal(String.valueOf(distanciaOrigenDeposito));
            tramo1.setDistanciaKm(distancia1BigDecimal);
            // calcular costo aprox BigDecimal
            BigDecimal costoAproxTramo1 = calcular_costo_aprox_tramo(distanciaOrigenDeposito, tarifa);
            tramo1.setCostoAprox(costoAproxTramo1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problema seteando distancia/ccostoAprox de tramo1 - calcular_ruta_doble()");
        }


        //setear tramo2
        //tramo2.setRuta(rutaDoble);
        tramo2.setNroOrden(1);
        tramo2.setTipoTramo("deposito-destino");
        tramo2.setDepositoOrigen(depositoIntermedio);
        tramo2.setEstado("no comenzado");
        // calcular la distancia para setearla luego
        try {
            DistanciaDTO depositoADestinoDTO = this.geoService.calcularDistancia(depositoIntermedio.getDireccionTxt(), destinoDireccion);
            double distanciaDepositoDestino = depositoADestinoDTO.getKilometros();
            BigDecimal distancia2BigDecimal = new BigDecimal(String.valueOf(distanciaDepositoDestino));
            tramo2.setDistanciaKm(distancia2BigDecimal);
            // calcular costo aprox BigDecimal
            BigDecimal costoAproxTramo2 = calcular_costo_aprox_tramo(distanciaDepositoDestino, tarifa);
            tramo2.setCostoAprox(costoAproxTramo2);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println("problema seteando distancia/ccostoAprox de tramo2 - calcular_ruta_doble()");
        }

        // calcular costo aprox BigDecimal

        //agregar a la ruta
        rutaDoble.agregarTramo(tramo1);
        rutaDoble.agregarTramo(tramo2);
        // calcular costo aprox como la suma de los dos costos aprox de sus tramos
        rutaDoble.setCostoAprox(tramo1.getCostoAprox().add(tramo2.getCostoAprox()));

        return rutaDoble;
    }

    private Ruta calcular_ruta_triple(Integer idSolicitud, String origenDireccion, String destinoDireccion, TarifaDTO tarifa) {
        
        Deposito depositoCercanoAlOrigen = obtenerDepositoMasCercano(origenDireccion);
        Deposito depositoCercanoAlDestino = obtenerDepositoMasCercano(destinoDireccion);

        if (depositoCercanoAlOrigen == null || depositoCercanoAlDestino == null || depositoCercanoAlDestino == depositoCercanoAlOrigen) {
            System.out.println("\n\nCalculo TRiple debbuging....\n\n");
            System.out.println("cercano al origen "+depositoCercanoAlOrigen.getNombre());
            System.out.println("cercano al destino "+depositoCercanoAlDestino.getNombre());
            System.out.println("==============================\n\n\n");
            return null;
        }

        Ruta rutaTramoTriple = new Ruta();
        Tramo tramo1 = new Tramo();
        Tramo tramo2 = new Tramo();
        Tramo tramo3 = new Tramo();

        // setear una ruta
        rutaTramoTriple.setIdSolicitud(idSolicitud);
        rutaTramoTriple.setCantTramos(3);
        rutaTramoTriple.setCantDepositos(2);

        //setear tramo 1
        //tramo1.setRuta(rutaTramoTriple);
        tramo1.setNroOrden(0);
        tramo1.setTipoTramo("origen-deposito");
        tramo1.setDepositoDestino(depositoCercanoAlOrigen);
        tramo1.setEstado("no comenzado");
        // calcular distancias y costos
        try {
            DistanciaDTO origenADepositoDTO = this.geoService.calcularDistancia(origenDireccion, depositoCercanoAlOrigen.getDireccionTxt());
            double distanciaOrigenDeposito = origenADepositoDTO.getKilometros();
            BigDecimal distancia1BigDecimal = new BigDecimal(String.valueOf(distanciaOrigenDeposito));
            tramo1.setDistanciaKm(distancia1BigDecimal);
            //calcular costo aprox
            BigDecimal costoAproxTramo1 = calcular_costo_aprox_tramo(distanciaOrigenDeposito ,tarifa);
            tramo1.setCostoAprox(costoAproxTramo1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problema seteando distancia/ccostoAprox de tramo1 - calcular_ruta_triple()");
        }

        //setear tramo 2
        //tramo2.setRuta(rutaTramoTriple);
        tramo2.setNroOrden(1);
        tramo2.setTipoTramo("deposito-deposito");
        tramo2.setDepositoOrigen(depositoCercanoAlOrigen);
        tramo2.setDepositoDestino(depositoCercanoAlDestino);
        tramo2.setEstado("no comenzado");
        // calcular distancias y costos
        try {
            String depositoCercanoAlOrigenDireccion = depositoCercanoAlOrigen.getDireccionTxt();
            String depositoCercanoAlDestinoDireccion = depositoCercanoAlDestino.getDireccionTxt();
            DistanciaDTO depositoADepositoDTO = this.geoService.calcularDistancia(depositoCercanoAlOrigenDireccion, depositoCercanoAlDestinoDireccion);
            double distanciaDepositoDeposito = depositoADepositoDTO.getKilometros();
            BigDecimal distancia2BigDecimal = new BigDecimal(String.valueOf(distanciaDepositoDeposito));
            tramo2.setDistanciaKm(distancia2BigDecimal);
            // calcular costo aprox
            BigDecimal costoAproxTramo2 = calcular_costo_aprox_tramo(distanciaDepositoDeposito, tarifa);
            tramo2.setCostoAprox(costoAproxTramo2);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problema seteando distancia/ccostoAprox de tramo2 - calcular_ruta_triple()");
        }


        //setar tramo 3
        //tramo3.setRuta(rutaTramoTriple);
        tramo3.setNroOrden(2);
        tramo3.setTipoTramo("deposito-destino");
        tramo3.setDepositoOrigen(depositoCercanoAlDestino);
        tramo3.setEstado("no comenzado");
        // calcular distancias y costos
        try {
            DistanciaDTO depositoADestinoDTO = this.geoService.calcularDistancia(depositoCercanoAlDestino.getDireccionTxt(), destinoDireccion);
            double distanciaDepositoDestino = depositoADestinoDTO.getKilometros();
            BigDecimal distancia3BigDecimal = new BigDecimal(String.valueOf(distanciaDepositoDestino));
            tramo3.setDistanciaKm(distancia3BigDecimal);
            // calcular costo aprox
            BigDecimal costoAproxTramo3 = calcular_costo_aprox_tramo(distanciaDepositoDestino, tarifa);
            tramo3.setCostoAprox(costoAproxTramo3);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problema seteando distancia/ccostoAprox de tramo3 - calcular_ruta_triple()");
        }

        //agregar a la ruta
        rutaTramoTriple.agregarTramo(tramo1);
        rutaTramoTriple.agregarTramo(tramo2);
        rutaTramoTriple.agregarTramo(tramo3);
        // calcular  costo aprox de ruta sumando los 3 costos aprox
        BigDecimal costoAproxRuta = tramo1.getCostoAprox().add( tramo2.getCostoAprox().add(tramo3.getCostoAprox()) );
        rutaTramoTriple.setCostoAprox(costoAproxRuta);

        return rutaTramoTriple;
    }

    private int transformarAHoras(String duracionString) {

        if (!duracionString.contains("hours")) {
            return 0; // si no dura por lo menos 1 hora devuelvo 0
        }

        // si dura mas que una hora, dividimos en un array el string de duracion
        String[] partes = duracionString.split(" ");

        if (duracionString.contains("days")) {
            return Integer.parseInt(partes[2])+48;
        }


        if (duracionString.contains("day")) {
            return Integer.parseInt(partes[2])+24;
        }


        // lo que esté en la primer posicion son las horas
        return Integer.parseInt(partes[0]);
    }

    private BigDecimal calcular_costo_aprox_tramo(double distanciaTramo, TarifaDTO tarifa) {
        // lamar a una fincion calcular_consumo_combustible_aprox que es un promedio del consumo de todos los camiones en actividad
        BigDecimal consumoCombustibleAprox = calcular_consumo_combustible_aprox();
        // de la tarifa que recibo como parametro me quedo con:
        //  ** costoBaseKMXVol   -- asumo que me pasan la tarifa correcta dado el contenedor de mi solicitud
        //  ** valor de combustible
        BigDecimal costoBaseKmXVol = tarifa.getCostoBaseKmXVol();
        BigDecimal valorCombustible = tarifa.getValorCombustible();

        BigDecimal distanciaBigDecimal = new BigDecimal(String.valueOf(distanciaTramo));

        // Costo estimado = distancia * ( costoBaseKMXVolumen + (valor de combustible * consumoCombustibleAproximado ) )

        BigDecimal costoAprox = distanciaBigDecimal.multiply( costoBaseKmXVol.add( valorCombustible.multiply( consumoCombustibleAprox) ) );

        return costoAprox;

    }

    public BigDecimal calcular_consumo_combustible_aprox() {

        List<Camion> camiones = camionService.listarTodos();
        if (camiones == null || camiones.isEmpty()) { return BigDecimal.ZERO; }

        BigDecimal sumaTotal = camiones.stream()
            .filter(camion -> camion.getEstado() != null && camion.getEstado().equalsIgnoreCase("Disponible"))
            // Mapeamos el objeto Camion a su campo BigDecimal (consumoXKm)
            .map(Camion::getConsumoXKm)
            // Filtramos posibles valores nulos antes de sumar
            .filter(consumo -> consumo != null) 
            // Utilizamos reduce para sumar todos los BigDecimal
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Obtener la cantidad de elementos (solo los que tenían consumoXKm no nulo)
        long cantidad = camiones.stream()
            .map(Camion::getConsumoXKm)
            .filter(consumo -> consumo != null)
            .count();
        
        if (cantidad == 0) {
            return BigDecimal.ZERO;
        }

        // hacemos estas operaciones para bigDecimal
        // igual que la precisión original del campo en la base de datos (precision = 10, scale = 3).
        BigDecimal promedio = sumaTotal.divide(
            BigDecimal.valueOf(cantidad), 3, // Escala deseada
            RoundingMode.HALF_UP
        );

        return promedio;
    }

    public Ruta calcularCostoReal(Ruta ruta) {
        // traer todos los tramos
        List<Tramo> tramos = tramoService.buscarPorRuta(ruta.getId());
        // sumar los costos reales
        BigDecimal totalCostoReal = tramos.stream()
            .map(tramo -> tramo.getCostoReal() != null ? tramo.getCostoReal() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // asignar el total
        ruta.setCostoReal(totalCostoReal);
        return ruta;
    }
}