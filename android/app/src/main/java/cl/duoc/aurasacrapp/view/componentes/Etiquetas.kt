package cl.duoc.aurasacrapp.view.componentes

import cl.duoc.aurasacrapp.model.EstadoApertura
import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.model.RecintoConComuna
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.TipoEvento
import cl.duoc.aurasacrapp.model.TipoRecinto

// Textos para mostrar en pantalla a partir de los enum del modelo

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

fun EstadoApertura.etiqueta(): String = when (this) {
    EstadoApertura.ABIERTO -> "Abierto"
    EstadoApertura.CERRADO -> "Cerrado"
    EstadoApertura.EN_CELEBRACION -> "En celebración"
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

// "Av. Los Aromos 1240" o "Calle Los Talleres s/n" si no tiene número
fun RecintoConComuna.direccion(): String = "$calle ${numeroCalle ?: "s/n"}"
