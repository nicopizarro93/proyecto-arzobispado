package cl.duoc.aurasacrapp_api.guia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadGuiaRepository extends JpaRepository<DisponibilidadGuia, Long> {

    List<DisponibilidadGuia> findByGuiaIdOrderByInicioAsc(Long idGuia);

    Optional<DisponibilidadGuia> findByIdAndGuiaId(Long id, Long idGuia);

    // ¿El guía tiene un bloque que cubra ese momento? (HU-33: guías disponibles en ese horario)
    boolean existsByGuiaIdAndInicioLessThanEqualAndFinGreaterThanEqual(
            Long idGuia, LocalDateTime momento, LocalDateTime mismoMomento);

    // Bloques que se cruzan con otro del mismo guía.
    List<DisponibilidadGuia> findByGuiaIdAndInicioLessThanAndFinGreaterThan(
            Long idGuia, LocalDateTime fin, LocalDateTime inicio);
}
