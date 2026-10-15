package cl.duoc.santiago_sacro_api.evento.dto;

import cl.duoc.santiago_sacro_api.evento.Evento;
import cl.duoc.santiago_sacro_api.evento.TipoEvento;
import cl.duoc.santiago_sacro_api.recinto.Recinto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Evento para la lista y el mapa: lleva las coordenadas de su recinto (HU-14). */
public record EventoResumenDto(
        Long id,
        TipoEvento tipo,
        String titulo,
        LocalDateTime inicio,
        LocalDateTime fin,
        Long idRecinto,
        String recinto,
        BigDecimal latitud,
        BigDecimal longitud) {

    public static EventoResumenDto desde(Evento e) {
        Recinto r = e.getRecinto();
        return new EventoResumenDto(e.getId(), e.getTipo(), e.getTitulo(), e.getInicio(), e.getFin(),
                r.getId(), r.getNombreOficial(), r.getLatitud(), r.getLongitud());
    }
}
