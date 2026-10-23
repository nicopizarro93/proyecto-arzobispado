package cl.duoc.santiagosacro.navigation

// Nombres de las rutas (pantallas) de la app.
// Tenerlos en un solo lugar evita errores de tipeo al navegar.
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val FICHA = "ficha/{recintoId}"   // {recintoId} es un argumento
    const val AGENDA = "agenda"
    const val EVENTO = "evento/{eventoId}"  // {eventoId} es un argumento
    const val ESCANEAR = "escanear"
    const val VISITAS = "visitas"
    const val PERFIL = "perfil"
    const val MI_RECINTO = "mi-recinto"     // solo para encargados

    // Arman la ruta con el id, por ejemplo "ficha/3" o "evento/8"
    fun ficha(recintoId: Long) = "ficha/$recintoId"
    fun evento(eventoId: Long) = "evento/$eventoId"
}
