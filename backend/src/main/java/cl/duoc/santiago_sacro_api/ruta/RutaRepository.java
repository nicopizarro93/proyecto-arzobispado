package cl.duoc.santiago_sacro_api.ruta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Rutas sugeridas activas con sus paradas y recintos, en una sola consulta.
    @Query("""
            select r from Ruta r
            left join fetch r.paradas p
            left join fetch p.recinto
            where r.tipo = cl.duoc.santiago_sacro_api.ruta.TipoRuta.SUGERIDA
              and r.activo = true
            order by r.nombre, p.id.orden
            """)
    List<Ruta> buscarSugeridasActivas();

    @Query("""
            select r from Ruta r
            left join fetch r.paradas p
            left join fetch p.recinto
            where r.id = :id
              and r.tipo = cl.duoc.santiago_sacro_api.ruta.TipoRuta.SUGERIDA
              and r.activo = true
            order by p.id.orden
            """)
    Optional<Ruta> buscarSugeridaActiva(@Param("id") Long id);
}