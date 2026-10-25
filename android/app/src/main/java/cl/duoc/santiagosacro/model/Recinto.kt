package cl.duoc.santiagosacro.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "recinto",
    foreignKeys = [
        ForeignKey(
            entity = Comuna::class,
            parentColumns = ["id"],
            childColumns = ["comunaId"]
        )
    ],
    indices = [Index("comunaId")]
)
data class Recinto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipoRecinto: TipoRecinto,
    val comunaId: Long,
    val nombreOficial: String,
    val calle: String,
    val numeroCalle: String?,          // null = sin número
    val latitud: Double,
    val longitud: Double,
    val descripcionHistorica: String?,
    val estadoApertura: EstadoApertura = EstadoApertura.CERRADO,
    val activo: Boolean = true         // false = dado de baja (borrado lógico)
)
