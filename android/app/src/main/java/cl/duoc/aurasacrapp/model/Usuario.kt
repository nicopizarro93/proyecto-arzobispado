package cl.duoc.aurasacrapp.model

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Copia local del usuario que inició sesión (el id es el mismo del backend).
// La contraseña nunca llega a la app: la valida el servidor.
@Entity(
    tableName = "usuario",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey
    val id: Long,
    val rol: Rol,
    val recintoId: Long?,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val institucion: String?
)
