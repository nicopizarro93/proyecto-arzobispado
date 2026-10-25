package cl.duoc.aurasacrapp.view.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.TipoEvento
import cl.duoc.aurasacrapp.model.TipoRecinto

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

fun EstadoApertura.etiqueta(): String = when (this) {
    EstadoApertura.ABIERTO -> "Abierto"
    EstadoApertura.CERRADO -> "Cerrado"
    EstadoApertura.EN_CELEBRACION -> "En celebración"
}

fun EstadoApertura.descripcion(): String = when (this) {
    EstadoApertura.ABIERTO -> "Recibe visitas en este momento"
    EstadoApertura.CERRADO -> "No recibe visitas por ahora"
    EstadoApertura.EN_CELEBRACION -> "Hay una misa o ceremonia en curso"
}

fun TipoEvento.etiqueta(): String = when (this) {
    TipoEvento.CONCIERTO -> "Concierto"
    TipoEvento.EXPOSICION -> "Exposición"
    TipoEvento.HITO_LITURGICO -> "Celebración litúrgica"
    TipoEvento.ACTIVIDAD_CULTURAL -> "Actividad cultural"
    TipoEvento.AVISO -> "Aviso"
    TipoEvento.APERTURA_ESPECIAL -> "Apertura especial"
    TipoEvento.CIERRE_ESPECIAL -> "Cierre especial"
}

fun ModoTraslado.etiqueta(): String = when (this) {
    ModoTraslado.A_PIE -> "A pie"
    ModoTraslado.TRANSPORTE_PUBLICO -> "Transporte público"
    ModoTraslado.VEHICULO -> "Vehículo"
}

fun ModoTraslado.icono(): ImageVector = when (this) {
    ModoTraslado.A_PIE -> Icons.AutoMirrored.Filled.DirectionsWalk
    ModoTraslado.TRANSPORTE_PUBLICO -> Icons.Filled.DirectionsBus
    ModoTraslado.VEHICULO -> Icons.Filled.DirectionsCar
}

// "Av. Los Aromos 1240" o "Calle Los Talleres s/n" si no tiene número
fun RecintoConComuna.direccion(): String = "$calle ${numeroCalle ?: "s/n"}"
