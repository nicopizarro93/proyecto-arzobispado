package cl.duoc.aurasacrapp_api.guia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SolicitudGuiaRepository extends JpaRepository<SolicitudGuia, Long> {

    // Solicitudes de un recinto (las que resuelve su encargado), de la visita más próxima a la más lejana.
    @Query("""
            select s from SolicitudGuia s
            join fetch s.visita v
            join fetch v.usuario
            left join fetch s.guia
            where s.recinto.id = :idRecinto
            order by v.inicio
            """)
    List<SolicitudGuia> buscarDelRecinto(@Param("idRecinto") Long idRecinto);

    // Una solicitud solo si es de ese recinto.
    @Query("""
            select s from SolicitudGuia s
            join fetch s.visita v
            join fetch v.usuario
            left join fetch s.guia
            where s.id = :id and s.recinto.id = :idRecinto
            """)
    Optional<SolicitudGuia> buscarUnaDelRecinto(@Param("id") Long id, @Param("idRecinto") Long idRecinto);

    // Mis solicitudes (HU-24): las de las visitas del usuario, las más recientes primero.
    @Query("""
            select s from SolicitudGuia s
            join fetch s.visita v
            join fetch s.recinto
            left join fetch s.guia
            where v.usuario.id = :idUsuario
            order by s.fechaSolicitud desc, s.id desc
            """)
    List<SolicitudGuia> buscarDelUsuario(@Param("idUsuario") Long idUsuario);

    // HU-23: no se pide dos veces un guía para el mismo recinto de la misma visita.
    boolean existsByVisitaIdAndRecintoId(Long idVisita, Long idRecinto);

    // ¿El guía ya está asignado a otra visita que empieza a esa misma hora?
    @Query("""
            select count(s) > 0 from SolicitudGuia s
            where s.guia.id = :idGuia
              and s.estado = cl.duoc.aurasacrapp_api.guia.EstadoSolicitud.ASIGNADA
              and s.visita.inicio = :inicio
              and s.id <> :idSolicitud
            """)
    boolean guiaOcupado(@Param("idGuia") Long idGuia,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("idSolicitud") Long idSolicitud);
}
