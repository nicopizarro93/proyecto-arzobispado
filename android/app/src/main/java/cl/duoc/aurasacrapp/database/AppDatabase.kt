package cl.duoc.aurasacrapp.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import cl.duoc.aurasacrapp.dao.ComunaDao
import cl.duoc.aurasacrapp.dao.EventoDao
import cl.duoc.aurasacrapp.dao.RecintoDao
import cl.duoc.aurasacrapp.dao.UsuarioDao
import cl.duoc.aurasacrapp.dao.VisitaDao
import cl.duoc.aurasacrapp.model.Comuna
import cl.duoc.aurasacrapp.model.Evento
import cl.duoc.aurasacrapp.model.Recinto
import cl.duoc.aurasacrapp.model.Usuario
import cl.duoc.aurasacrapp.model.Visita
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [Comuna::class, Recinto::class, Usuario::class, Visita::class, Evento::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comunaDao(): ComunaDao
    abstract fun recintoDao(): RecintoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun visitaDao(): VisitaDao
    abstract fun eventoDao(): EventoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aura_sacrapp.db"
                )
                    // Room 3 necesita un driver SQLite: usamos el que viene incluido en la app
                    .setDriver(BundledSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    // Mientras desarrollamos: si cambia la versión, borra y recrea la BD
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}