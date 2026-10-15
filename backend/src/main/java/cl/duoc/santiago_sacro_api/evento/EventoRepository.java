package cl.duoc.santiago_sacro_api.evento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Publicados y vigentes, de recintos activos. Tipo y recinto son filtros opcionales.
    // Vigente: aún no termina; si no tiene término, durante 24 horas desde su inicio.
    @Query("""
            select e from Evento e
            join fetch e.recinto r
            join fetch r.comuna
            where e.estado = cl.duoc.santiago_sacro_api.evento.EstadoEvento.PUBLICADO
              and r.activo = true
              and (e.fin >= :ahora or (e.fin is null and e.inicio >= :limiteSinFin))
              and (:tipo is null or e.tipo = :tipo)
              and (:idRecinto is null or r.id = :idRecinto)
            order by e.inicio
            """)
    List<Evento> buscarVigentes(@Param("ahora") LocalDateTime ahora,
                                @Param("limiteSinFin") LocalDateTime limiteSinFin,
                                @Param("tipo") TipoEvento tipo,
                                @Param("idRecinto") Long idRecinto);

    @Query("""
            select e from Evento e
            join fetch e.recinto r
            join fetch r.comuna
            where e.id = :id
              and e.estado = cl.duoc.santiago_sacro_api.evento.EstadoEvento.PUBLICADO
              and r.activo = true
            """)
    Optional<Evento> buscarPublicadoPorId(@Param("id") Long id);
}
