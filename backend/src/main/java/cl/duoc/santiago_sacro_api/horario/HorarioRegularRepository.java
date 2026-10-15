package cl.duoc.santiago_sacro_api.horario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioRegularRepository extends JpaRepository<HorarioRegular, Long> {

    // Bloques de un recinto, ordenados por día y hora de apertura.
    List<HorarioRegular> findByRecintoIdOrderByDiaSemanaAscHoraAperturaAsc(Long idRecinto);
}
