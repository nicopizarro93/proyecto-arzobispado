package cl.duoc.santiago_sacro_api.multimedia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecursoMultimediaRepository extends JpaRepository<RecursoMultimedia, Long> {

    // Galería de un recinto, en el orden en que se muestra.
    List<RecursoMultimedia> findByRecintoIdOrderByOrdenAsc(Long idRecinto);
}
