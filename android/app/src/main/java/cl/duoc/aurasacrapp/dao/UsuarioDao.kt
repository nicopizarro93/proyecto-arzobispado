package cl.duoc.aurasacrapp.dao

import androidx.room3.Dao
import androidx.room3.Upsert
import cl.duoc.aurasacrapp.model.Usuario

@Dao
interface UsuarioDao {

    // Inserta el usuario o, si ya existe (mismo id), lo actualiza
    @Upsert
    suspend fun usuarioGuardar(usuario: Usuario)
}
