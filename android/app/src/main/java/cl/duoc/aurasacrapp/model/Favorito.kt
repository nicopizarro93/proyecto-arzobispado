package cl.duoc.aurasacrapp.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

// Recinto que un usuario marcó como favorito. Se guarda solo en el teléfono (Room): funciona
// sin conexión y se mantiene al cerrar la app. La clave es el par (usuario, recinto).
@Entity(
    tableName = "favorito",
    primaryKeys = ["usuarioId", "recintoId"],
    foreignKeys = [
        ForeignKey(
            entity = Recinto::class,
            parentColumns = ["id"],
            childColumns = ["recintoId"]
        )
    ],
    indices = [Index(value = ["recintoId"])]
)
data class Favorito(
    val usuarioId: Long,
    val recintoId: Long,
    val fecha: Long = System.currentTimeMillis()      // cuándo se marcó, en milisegundos
)
