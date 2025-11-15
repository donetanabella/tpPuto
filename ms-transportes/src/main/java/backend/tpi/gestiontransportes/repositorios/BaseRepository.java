package backend.tpi.gestiontransportes.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    default T guardar(T entidad) { return save(entidad); }

    default Optional<T> buscarPorId(ID id) { return findById(id); }

    default List<T> listarTodos() { return findAll(); }

    default Stream<T> listarStream() { return findAll().stream(); }

    default void eliminarPorId(ID id) { deleteById(id); }

    default Optional<T> modificar(ID id, T entidadNueva) {
        if (existsById(id)) {
            return Optional.of(save(entidadNueva));
        } else {
            return Optional.empty();
        }
    }

    default boolean existe(ID id) { return existsById(id); }

    default long contar() { return count(); }
}

