package cl.duoc.santiagosacro.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.santiagosacro.dao.ComunaDao
import cl.duoc.santiagosacro.dao.RecintoDao
import cl.duoc.santiagosacro.dao.UsuarioDao
import cl.duoc.santiagosacro.dao.VisitaDao
import cl.duoc.santiagosacro.model.Comuna
import cl.duoc.santiagosacro.model.Recinto
import cl.duoc.santiagosacro.model.Usuario
import cl.duoc.santiagosacro.model.Visita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Comuna::class, Recinto::class, Usuario::class, Visita::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comunaDao(): ComunaDao
    abstract fun recintoDao(): RecintoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun visitaDao(): VisitaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "santiago_sacro.db"
                )
                    // Mientras desarrollamos: si cambia la versión, borra y recrea la BD
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                cargarDatosIniciales(instance)
                instance
            }
        }

        // Al abrir la app: si la BD está vacía (recién instalada o recreada al subir la versión),
        // carga los datos iniciales. Se hace en segundo plano para no congelar la pantalla.
        private fun cargarDatosIniciales(db: AppDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    DatosIniciales.cargarSiEstaVacia(db)
                } catch (e: Exception) {
                    Log.e("SantiagoSacro", "No se pudieron cargar los datos iniciales", e)
                }
            }
        }
    }
}