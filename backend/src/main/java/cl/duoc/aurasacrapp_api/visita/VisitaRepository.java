package cl.duoc.aurasacrapp_api.visita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VisitaRepository extends JpaRepository<Visita, Long> {

    // Mis visitas, con su recinto o su ruta, de la más lejana a la más antigua.
    @Query("""
            select v from Visita v
            left join fetch v.recinto
            left join fetch v.ruta
            where v.usuario.id = :idUsuario
            order by v.inicio desc
            """)
    List<Visita> buscarDelUsuario(@Param("idUsuario") Long idUsuario);

    // Una visita solo si es del usuario.
    @Query("""
            select v from Visita v
            left join fetch v.recinto
            left join fetch v.ruta
            where v.id = :id and v.usuario.id = :idUsuario
            """)
    Optional<Visita> buscarUnaDelUsuario(@Param("id") Long id, @Param("idUsuario") Long idUsuario);

    // HU-25: al cancelar una visita se cancelan sus solicitudes de guía pendientes o asignadas.
    // Es SQL directo porque la entidad SolicitudGuia llegará con HU-23 (segunda entrega).
    @Modifying
    @Query(value = """
            UPDATE solicitud_guia SET estado = 'CANCELADA'
            WHERE id_visita = :idVisita AND estado IN ('PENDIENTE', 'ASIGNADA')
            """, nativeQuery = true)
    int cancelarSolicitudesDeGuia(@Param("idVisita") Long idVisita);
}