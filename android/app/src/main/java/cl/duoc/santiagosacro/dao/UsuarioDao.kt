package cl.duoc.santiagosacro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.santiagosacro.model.Rol
import cl.duoc.santiagosacro.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun usuarioAlmacenar(usuario: Usuario)

    @Insert
    suspend fun usuarioAlmacenarTodos(usuarios: List<Usuario>)

    @Update
    suspend fun usuarioActualizar(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE correo = :correo")
    suspend fun usuarioBuscarPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuario WHERE rol = :rol ORDER BY apellido, nombre")
    fun usuarioListarPorRol(rol: Rol): Flow<List<Usuario>>
}