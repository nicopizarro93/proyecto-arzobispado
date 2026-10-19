package cl.duoc.santiagosacro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.santiagosacro.dao.ComunaDao
import cl.duoc.santiagosacro.dao.RecintoDao
import cl.duoc.santiagosacro.model.Comuna
import cl.duoc.santiagosacro.model.Recinto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Comuna::class, Recinto::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun comunaDao(): ComunaDao
    abstract fun recintoDao(): RecintoDao

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
                    .addCallback(object : RoomDatabase.Callback() {
                        // Cuando la BD se crea por primera vez
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            cargarDatosIniciales()
                        }

                        // Cuando se recrea porque subimos la versión
                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            cargarDatosIniciales()
                        }
                    })
                    // Mientras desarrollamos: si cambia la versión, borra y recrea la BD
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun cargarDatosIniciales() {
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { DatosIniciales.cargar(it) }
            }
        }
    }
}