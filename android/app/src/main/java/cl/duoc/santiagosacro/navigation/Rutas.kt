package cl.duoc.santiagosacro.navigation

// Nombres de las rutas (pantallas) de la app.
// Tenerlos en un solo lugar evita errores de tipeo al navegar.
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val FICHA = "ficha/{recintoId}"   // {recintoId} es un argumento
    const val ESCANEAR = "escanear"
    const val VISITAS = "visitas"
    const val PERFIL = "perfil"

    // Arma la ruta de la ficha con el id del recinto, por ejemplo "ficha/3"
    fun ficha(recintoId: Long) = "ficha/$recintoId"
}
