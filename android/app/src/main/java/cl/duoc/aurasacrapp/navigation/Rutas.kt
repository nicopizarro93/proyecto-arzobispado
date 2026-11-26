package cl.duoc.aurasacrapp.navigation

// Nombres de las rutas (pantallas) de la app.
// Tenerlos en un solo lugar evita errores de tipeo al navegar.
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val MAPA = "mapa"
    const val FICHA = "ficha/{recintoId}"   // {recintoId} es un argumento
    const val PLANIFICAR = "planificar/{recintoId}"
    const val RUTAS = "rutas"
    const val RUTA = "ruta/{rutaId}"
    const val PLANIFICAR_RUTA = "planificar-ruta/{rutaId}"
    const val CREAR_RUTA = "crear-ruta"
    const val AGENDA = "agenda"
    const val EVENTO = "evento/{eventoId}"  // {eventoId} es un argumento
    const val ESCANEAR = "escanear"
    const val VISITAS = "visitas"
    const val PERFIL = "perfil"
    const val MI_RECINTO = "mi-recinto"     // solo para encargados
    const val PUBLICAR_EVENTO = "publicar-evento"
    const val MIS_EVENTOS = "mis-eventos"
    const val EDITAR_EVENTO = "editar-evento/{eventoId}"
    const val EDITAR_FICHA = "editar-ficha"
    const val GUIAS = "guias"
    const val GUIA_DISPONIBILIDAD = "guia/{guiaId}"

    // Arman la ruta con el id, por ejemplo "ficha/3" o "evento/8"
    fun ficha(recintoId: Long) = "ficha/$recintoId"
    fun evento(eventoId: Long) = "evento/$eventoId"
    fun planificar(recintoId: Long) = "planificar/$recintoId"
    fun ruta(rutaId: Long) = "ruta/$rutaId"
    fun planificarRuta(rutaId: Long) = "planificar-ruta/$rutaId"
    fun editarEvento(eventoId: Long) = "editar-evento/$eventoId"
    fun guiaDisponibilidad(guiaId: Long) = "guia/$guiaId"
}
