package cl.duoc.santiagosacro.view.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import cl.duoc.santiagosacro.model.RecintoConComuna
import cl.duoc.santiagosacro.model.Rol
import cl.duoc.santiagosacro.model.TipoRecinto

// Textos e íconos para mostrar en pantalla a partir de los enum del modelo

fun Rol.etiqueta(): String = when (this) {
    Rol.VISITANTE -> "Visitante"
    Rol.DOCENTE -> "Docente"
    Rol.ENCARGADO -> "Encargado de recinto"
    Rol.ADMINISTRADOR -> "Administrador"
}

fun TipoRecinto.etiqueta(): String = when (this) {
    TipoRecinto.TEMPLO -> "Templo"
    TipoRecinto.MUSEO -> "Museo"
    TipoRecinto.COLEGIO -> "Colegio"
    TipoRecinto.ARCHIVO -> "Archivo"
    TipoRecinto.INSTITUCION -> "Institución"
}

fun TipoRecinto.etiquetaPlural(): String = when (this) {
    TipoRecinto.TEMPLO -> "Templos"
    TipoRecinto.MUSEO -> "Museos"
    TipoRecinto.COLEGIO -> "Colegios"
    TipoRecinto.ARCHIVO -> "Archivos"
    TipoRecinto.INSTITUCION -> "Instituciones"
}

fun TipoRecinto.icono(): ImageVector = when (this) {
    TipoRecinto.TEMPLO -> Icons.Filled.Church
    TipoRecinto.MUSEO -> Icons.Filled.Museum
    TipoRecinto.COLEGIO -> Icons.Filled.School
    TipoRecinto.ARCHIVO -> Icons.Filled.Archive
    TipoRecinto.INSTITUCION -> Icons.Filled.AccountBalance
}

// "Av. Los Aromos 1240" o "Calle Los Talleres s/n" si no tiene número
fun RecintoConComuna.direccion(): String = "$calle ${numeroCalle ?: "s/n"}"
