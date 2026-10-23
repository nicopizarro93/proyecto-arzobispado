package cl.duoc.santiagosacro.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Evento o aviso que publica un recinto (concierto, exposición, misa, cierre especial...)
@Entity(
    tableName = "evento",
    foreignKeys = [
        ForeignKey(
            entity = Recinto::class,
            parentColumns = ["id"],
            childColumns = ["recintoId"]
        )
    ],
    indices = [Index(value = ["recintoId"]), Index(value = ["fechaHoraInicio"])]
)
data class Evento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recintoId: Long,
    val tipoEvento: TipoEvento,
    val titulo: String,
    val descripcion: String?,
    val fechaHoraInicio: Long,          // en milisegundos
    val fechaHoraFin: Long?,            // null = sin hora de término (vale todo el día)
    val estado: EstadoEvento = EstadoEvento.PUBLICADO
)
