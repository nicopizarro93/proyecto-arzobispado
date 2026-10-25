package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.Usuario
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