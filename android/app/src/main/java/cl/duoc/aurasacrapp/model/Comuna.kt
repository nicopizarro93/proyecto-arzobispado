package cl.duoc.aurasacrapp.model

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "comuna",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Comuna(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)
