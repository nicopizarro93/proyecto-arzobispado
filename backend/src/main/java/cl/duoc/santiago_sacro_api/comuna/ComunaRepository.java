package cl.duoc.santiago_sacro_api.comuna;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComunaRepository extends JpaRepository<Comuna, Long> {

    List<Comuna> findAllByOrderByNombreAsc();
}