package backend.tpi.gestiondesolicitudes.repositorios;

import java.math.BigDecimal;
import java.util.List;

import backend.tpi.gestiondesolicitudes.domain.Tarifa;

public interface TarifaRepository extends BaseRepository<Tarifa, Integer> {
    /**
     * Busca tarifas donde el volumen proporcionado esté dentro del rango [volMin, volMax].
     * Se traduce a: WHERE :volumen >= volMin AND :volumen <= volMax
     *
     * :param volumen El valor de volumen a buscar dentro del rango.
     * :return Una lista de Tarifas que cumplen con el rango.
     */
    List<Tarifa> findByVolMinLessThanEqualAndVolMaxGreaterThan(BigDecimal volumen, BigDecimal volumen2);
    // necesitas el parámetro 'volumen' dos veces para que JPA lo use en ambas comparaciones, para el <= y para el >=.
}
