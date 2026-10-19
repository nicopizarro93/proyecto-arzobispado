package cl.duoc.santiagosacro.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comuna",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Comuna(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)
