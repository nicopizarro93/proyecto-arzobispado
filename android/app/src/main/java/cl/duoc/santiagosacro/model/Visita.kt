package cl.duoc.santiagosacro.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Una visita registrada al escanear el código QR de un recinto
@Entity(
    tableName = "visita",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"]
        ),
        ForeignKey(
            entity = Recinto::class,
            parentColumns = ["id"],
            childColumns = ["recintoId"]
        )
    ],
    indices = [Index(value = ["usuarioId"]), Index(value = ["recintoId"])]
)
data class Visita(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val recintoId: Long,
    val fechaHora: Long          // momento del escaneo en milisegundos (System.currentTimeMillis())
)
