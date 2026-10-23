package cl.duoc.santiagosacro.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuario",
    foreignKeys = [
        ForeignKey(
            entity = Recinto::class,
            parentColumns = ["id"],
            childColumns = ["recintoId"]
        )
    ],
    indices = [Index(value = ["recintoId"]), Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rol: Rol,
    val recintoId: Long?,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val passwordHash: String,
    val institucion: String?,
    val activo: Boolean = true
)