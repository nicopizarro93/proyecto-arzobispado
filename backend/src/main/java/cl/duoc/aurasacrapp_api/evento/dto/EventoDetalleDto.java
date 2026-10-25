package cl.duoc.aurasacrapp_api.evento.dto;

import cl.duoc.aurasacrapp_api.evento.Evento;
import cl.duoc.aurasacrapp_api.evento.TipoEvento;
import cl.duoc.aurasacrapp_api.recinto.Recinto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Detalle de un evento (HU-15): con la dirección y las coordenadas para "Cómo llegar". */
public record EventoDetalleDto(
        Long id,
        TipoEvento tipo,
        String titulo,
        String descripcion,
        LocalDateTime inicio,
        LocalDateTime fin,
        Long idRecinto,
        String recinto,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud) {

    public static EventoDetalleDto desde(Evento e) {
        Recinto r = e.getRecinto();
        return new EventoDetalleDto(e.getId(), e.getTipo(), e.getTitulo(), e.getDescripcion(),
                e.getInicio(), e.getFin(), r.getId(), r.getNombreOficial(), r.direccionCompleta(),
                r.getLatitud(), r.getLongitud());
    }
}