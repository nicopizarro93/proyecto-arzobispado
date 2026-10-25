package cl.duoc.aurasacrapp.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

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