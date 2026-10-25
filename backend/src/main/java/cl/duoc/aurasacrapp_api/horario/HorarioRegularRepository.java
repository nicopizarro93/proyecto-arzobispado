package cl.duoc.aurasacrapp_api.horario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioRegularRepository extends JpaRepository<HorarioRegular, Long> {

    // Bloques de un recinto, ordenados por día y hora de apertura.
    List<HorarioRegular> findByRecintoIdOrderByDiaSemanaAscHoraAperturaAsc(Long idRecinto);

    // Un bloque solo si pertenece a ese recinto.
    Optional<HorarioRegular> findByIdAndRecintoId(Long id, Long idRecinto);

    // Bloques de un día, para revisar traslapes.
    List<HorarioRegular> findByRecintoIdAndDiaSemana(Long idRecinto, Integer diaSemana);
}
