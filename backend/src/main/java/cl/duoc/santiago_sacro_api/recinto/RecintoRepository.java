package cl.duoc.santiago_sacro_api.recinto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecintoRepository extends JpaRepository<Recinto, Long> {

    // Cada filtro es opcional: si llega null, esa condición no se aplica.
    @Query("""
            select r from Recinto r
            join fetch r.comuna c
            where r.activo = true
              and (:texto is null or r.nombreOficial like concat('%', :texto, '%'))
              and (:idComuna is null or c.id = :idComuna)
              and (:tipo is null or r.tipoRecinto = :tipo)
              and (:estado is null or r.estadoApertura = :estado)
            order by r.nombreOficial
            """)
    List<Recinto> buscar(@Param("texto") String texto,
                         @Param("idComuna") Long idComuna,
                         @Param("tipo") TipoRecinto tipo,
                         @Param("estado") EstadoApertura estado);

    @Query("select r from Recinto r join fetch r.comuna where r.id = :id and r.activo = true")
    Optional<Recinto> buscarActivoPorId(@Param("id") Long id);
}

/*
join fetch trae la comuna en la misma consulta. Así evitamos una consulta extra por cada recinto, y el error de carga perezosa fuera de la transacción.
La búsqueda por nombre no distingue mayúsculas ni tildes gracias a la intercalación de la base (utf8mb4_0900_ai_ci): “jose” encuentra “José”.
 */
