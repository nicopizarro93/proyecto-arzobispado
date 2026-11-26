package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import cl.duoc.aurasacrapp.model.Recinto
import cl.duoc.aurasacrapp.model.RecintoConComuna
import kotlinx.coroutines.flow.Flow

@Dao
interface RecintoDao {

    // Sincronización con el backend: inserta los recintos nuevos y actualiza los que ya existen
    @Upsert
    suspend fun recintoGuardarTodos(recintos: List<Recinto>)

    // Los recintos que el backend ya no entrega se marcan como inactivos (no se borran:
    // otras tablas, como las visitas, pueden apuntar a ellos)
    @Query("UPDATE recinto SET activo = 0 WHERE id NOT IN (:ids)")
    suspend fun recintoDesactivarExcepto(ids: List<Long>)

    // Copia de todos los recintos guardados (para conservar la historia al sincronizar)
    @Query("SELECT * FROM recinto")
    suspend fun recintoListarTodos(): List<Recinto>

    @Query("UPDATE recinto SET descripcionHistorica = :descripcion WHERE id = :id")
    suspend fun recintoActualizarDescripcion(id: Long, descripcion: String?)

    @Update
    suspend fun recintoActualizar(recinto: Recinto)

    // Solo los activos: los dados de baja no se muestran en la app
    @Query("SELECT * FROM recinto WHERE activo = 1 ORDER BY nombreOficial")
    fun recintoListar(): Flow<List<Recinto>>

    @Query("SELECT * FROM recinto WHERE id = :id")
    fun recintoBuscarPorId(id: Long): Flow<Recinto?>

    // Para el QR: consulta única (suspend) de un recinto activo
    @Query("SELECT * FROM recinto WHERE id = :id AND activo = 1")
    suspend fun recintoObtenerActivo(id: Long): Recinto?

    // Lista de Inicio: recintos activos con el nombre de su comuna (JOIN)
    @Query(
        """
        SELECT r.id, r.tipoRecinto, r.nombreOficial, c.nombre AS comuna, r.direccion,
               r.latitud, r.longitud, r.descripcionHistorica, r.estadoApertura
        FROM recinto r
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE r.activo = 1
        ORDER BY r.nombreOficial
        """
    )
    fun recintoListarConComuna(): Flow<List<RecintoConComuna>>

    // Ficha: un recinto activo con el nombre de su comuna
    @Query(
        """
        SELECT r.id, r.tipoRecinto, r.nombreOficial, c.nombre AS comuna, r.direccion,
               r.latitud, r.longitud, r.descripcionHistorica, r.estadoApertura
        FROM recinto r
        INNER JOIN comuna c ON c.id = r.comunaId
        WHERE r.id = :id AND r.activo = 1
        """
    )
    fun recintoBuscarConComuna(id: Long): Flow<RecintoConComuna?>
}