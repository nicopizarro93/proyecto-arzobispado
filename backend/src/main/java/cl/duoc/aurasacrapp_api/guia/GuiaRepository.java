package cl.duoc.aurasacrapp_api.guia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuiaRepository extends JpaRepository<Guia, Long> {

    // Guías de un recinto, por orden alfabético.
    List<Guia> findByRecintoIdOrderByNombreAscApellidoAsc(Long idRecinto);

    // Un guía solo si pertenece a ese recinto: un encargado nunca ve los de otro.
    Optional<Guia> findByIdAndRecintoId(Long id, Long idRecinto);
}
