package cl.duoc.aurasacrapp_api.evento.dto;

import cl.duoc.aurasacrapp_api.evento.Evento;
import cl.duoc.aurasacrapp_api.evento.TipoEvento;

import java.time.LocalDateTime;

/** Apertura o cierre excepcional que se muestra junto al horario de la ficha (HU-13, HU-30). */
public record ExcepcionHorarioDto(Long id, TipoEvento tipo, String motivo,
                                  LocalDateTime inicio, LocalDateTime fin) {

    public static ExcepcionHorarioDto desde(Evento e) {
        return new ExcepcionHorarioDto(e.getId(), e.getTipo(), e.getTitulo(), e.getInicio(), e.getFin());
    }
}