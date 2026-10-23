package cl.duoc.santiagosacro.database

import androidx.room.withTransaction
import cl.duoc.santiagosacro.model.Comuna
import cl.duoc.santiagosacro.model.EstadoApertura
import cl.duoc.santiagosacro.model.Recinto
import cl.duoc.santiagosacro.model.Rol
import cl.duoc.santiagosacro.model.TipoRecinto
import cl.duoc.santiagosacro.model.Usuario
import cl.duoc.santiagosacro.util.hashPassword

// Datos iniciales: los mismos del backend (V2 y V3), con los mismos id
object DatosIniciales {

    // id de las comunas que usan los recintos (= posición en la lista de abajo)
    private const val CERRILLOS = 2L
    private const val LO_ESPEJO = 16L
    private const val MAIPU = 19L
    private const val RECOLETA = 27L
    private const val PUENTE_ALTO = 33L

    // V2__datos_iniciales.sql: el id es la posición en la lista (1, 2, 3...)
    private val comunas = listOf(
        "Santiago", "Cerrillos", "Cerro Navia", "Conchalí", "El Bosque",
        "Estación Central", "Huechuraba", "Independencia", "La Cisterna", "La Florida",
        "La Granja", "La Pintana", "La Reina", "Las Condes", "Lo Barnechea",
        "Lo Espejo", "Lo Prado", "Macul", "Maipú", "Ñuñoa",
        "Pedro Aguirre Cerda", "Peñalolén", "Providencia", "Pudahuel", "Quilicura",
        "Quinta Normal", "Recoleta", "Renca", "San Joaquín", "San Miguel",
        "San Ramón", "Vitacura", "Puente Alto"
    ).mapIndexed { i, nombre -> Comuna(id = i + 1L, nombre = nombre) }

    // V3__datos_prueba_recintos.sql
    private val recintos = listOf(
        Recinto(
            id = 1, tipoRecinto = TipoRecinto.TEMPLO, comunaId = MAIPU,
            nombreOficial = "Capilla del Buen Pastor",
            calle = "Pasaje El Llano", numeroCalle = "118",
            latitud = -33.509700, longitud = -70.757400,
            descripcionHistorica = "Levantada a fines del siglo XIX por los vecinos del antiguo barrio del Llano. Conserva su fachada de albañilería y un campanario de madera restaurado.",
            estadoApertura = EstadoApertura.CERRADO
        ),
        Recinto(
            id = 2, tipoRecinto = TipoRecinto.TEMPLO, comunaId = MAIPU,
            nombreOficial = "Templo San Lucas del Llano",
            calle = "Av. Los Aromos", numeroCalle = "1240",
            latitud = -33.511200, longitud = -70.752900,
            descripcionHistorica = "Templo de una nave construido a comienzos del siglo XX, con vitrales donados por familias del sector.",
            estadoApertura = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 3, tipoRecinto = TipoRecinto.MUSEO, comunaId = MAIPU,
            nombreOficial = "Museo de Arte Sacro Los Aromos",
            calle = "Av. Los Aromos", numeroCalle = "1302",
            latitud = -33.512600, longitud = -70.750100,
            descripcionHistorica = "Reúne imaginería, platería y ornamentos litúrgicos de parroquias del poniente de Santiago.",
            estadoApertura = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 4, tipoRecinto = TipoRecinto.TEMPLO, comunaId = CERRILLOS,
            nombreOficial = "Parroquia Santa Rosa de la Cañada",
            calle = "Calle La Cañada", numeroCalle = "77",
            latitud = -33.495800, longitud = -70.716300,
            descripcionHistorica = "Parroquia de adobe y madera que mantiene su retablo original y una tradicional fiesta patronal.",
            estadoApertura = EstadoApertura.EN_CELEBRACION
        ),
        Recinto(
            id = 5, tipoRecinto = TipoRecinto.ARCHIVO, comunaId = RECOLETA,
            nombreOficial = "Archivo Histórico La Chimba",
            calle = "Calle Dominica", numeroCalle = "540",
            latitud = -33.428900, longitud = -70.646200,
            descripcionHistorica = "Custodia libros parroquiales, planos y fotografías de las capillas del sector norte.",
            estadoApertura = EstadoApertura.CERRADO
        ),
        Recinto(
            id = 6, tipoRecinto = TipoRecinto.TEMPLO, comunaId = PUENTE_ALTO,
            nombreOficial = "Capilla San José Obrero",
            calle = "Pasaje Los Olivos", numeroCalle = "320",
            latitud = -33.611700, longitud = -70.575800,
            descripcionHistorica = "Capilla levantada por trabajadores del barrio a mediados del siglo XX, con murales de la vida de San José.",
            estadoApertura = EstadoApertura.ABIERTO
        ),
        Recinto(
            id = 7, tipoRecinto = TipoRecinto.COLEGIO, comunaId = PUENTE_ALTO,
            nombreOficial = "Colegio San Esteban",
            calle = "Calle Los Talleres", numeroCalle = null,
            latitud = -33.604500, longitud = -70.581200,
            descripcionHistorica = "Colegio con una capilla interior y un pequeño museo escolar abiertos a visitas guiadas.",
            estadoApertura = EstadoApertura.CERRADO
        ),
        Recinto(
            id = 8, tipoRecinto = TipoRecinto.TEMPLO, comunaId = LO_ESPEJO,
            nombreOficial = "Capilla Antigua del Molino",
            calle = "Camino El Molino", numeroCalle = "15",
            latitud = -33.520300, longitud = -70.690800,
            descripcionHistorica = "Recinto dado de baja: sirve para comprobar que los inactivos no aparecen en la app.",
            estadoApertura = EstadoApertura.CERRADO,
            activo = false
        )
    )

    // Todas las cuentas de prueba usan la contraseña Prueba123 (guardada como hash)
    private val claveDePrueba = hashPassword("Prueba123")

    // V4 y V8 del backend: mismos usuarios y mismos id
    private val usuarios = listOf(
        Usuario(
            id = 1, rol = Rol.ADMINISTRADOR, recintoId = null,
            nombre = "Ana", apellido = "Rojas", correo = "admin@santiagosacro.cl",
            passwordHash = claveDePrueba, institucion = null
        ),
        Usuario(
            id = 2, rol = Rol.ENCARGADO, recintoId = 1,   // Capilla del Buen Pastor
            nombre = "Pedro", apellido = "Soto", correo = "encargado.capilla@santiagosacro.cl",
            passwordHash = claveDePrueba, institucion = null
        ),
        Usuario(
            id = 3, rol = Rol.ENCARGADO, recintoId = 3,   // Museo de Arte Sacro Los Aromos
            nombre = "Carmen", apellido = "Muñoz", correo = "encargado.museo@santiagosacro.cl",
            passwordHash = claveDePrueba, institucion = null
        ),
        Usuario(
            id = 4, rol = Rol.VISITANTE, recintoId = null,
            nombre = "Lucía", apellido = "Herrera", correo = "visitante@santiagosacro.cl",
            passwordHash = claveDePrueba, institucion = null
        ),
        Usuario(
            id = 5, rol = Rol.DOCENTE, recintoId = null,
            nombre = "Jorge", apellido = "Contreras", correo = "docente@santiagosacro.cl",
            passwordHash = claveDePrueba, institucion = "Colegio San Esteban"
        )
    )

    // El orden importa por las FK: comuna <- recinto <- usuario (encargados).
    // withTransaction: se guarda todo o nada; si algo falla, la BD no queda a medias.
    suspend fun cargar(db: AppDatabase) {
        db.withTransaction {
            db.comunaDao().comunaAlmacenarTodas(comunas)
            db.recintoDao().recintoAlmacenarTodos(recintos)
            db.usuarioDao().usuarioAlmacenarTodos(usuarios)
        }
    }
}