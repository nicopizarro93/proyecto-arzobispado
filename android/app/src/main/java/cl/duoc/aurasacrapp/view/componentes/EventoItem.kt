package cl.duoc.aurasacrapp.view.componentes

import androidx.compose.runtime.Composable
import cl.duoc.aurasacrapp.model.EventoDetalle
import cl.duoc.aurasacrapp.util.formatearRango

// Tarjeta de un evento: se usa en la Agenda y en la Ficha del recinto
@Composable
fun EventoItem(evento: EventoDetalle, mostrarRecinto: Boolean, onClick: () -> Unit) {
    val lugar = if (mostrarRecinto) "\n${evento.nombreRecinto} · ${evento.comuna}" else ""
    Item(
        titulo = evento.titulo,
        detalle = "${evento.tipoEvento.etiqueta()} · ${formatearRango(evento.fechaHoraInicio, evento.fechaHoraFin)}$lugar",
        onClick = onClick
    )
}
