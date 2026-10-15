package cl.duoc.santiago_sacro_api.evento.dto;

import cl.duoc.santiago_sacro_api.evento.EstadoEvento;
import cl.duoc.santiago_sacro_api.evento.Evento;
import cl.duoc.santiago_sacro_api.evento.TipoEvento;

import java.time.LocalDateTime;

/** Evento visto por el encargado: incluye el estado, para el historial con los cancelados (HU-29). */
public record EventoGestionDto(Long id, TipoEvento tipo, String titulo, String descripcion,
                               LocalDateTime inicio, LocalDateTime fin, EstadoEvento estado) {

    public static EventoGestionDto desde(Evento e) {
        return new EventoGestionDto(e.getId(), e.getTipo(), e.getTitulo(), e.getDescripcion(),
                e.getInicio(), e.getFin(), e.getEstado());
    }
}
