package cl.duoc.santiagosacro.view.componentes

import cl.duoc.santiagosacro.model.Rol

// Textos para mostrar en pantalla a partir de los enum del modelo
fun Rol.etiqueta(): String = when (this) {
    Rol.VISITANTE -> "Visitante"
    Rol.DOCENTE -> "Docente"
    Rol.ENCARGADO -> "Encargado de recinto"
    Rol.ADMINISTRADOR -> "Administrador"
}
