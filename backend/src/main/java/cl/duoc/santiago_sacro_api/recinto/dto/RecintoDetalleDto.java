package cl.duoc.santiago_sacro_api.recinto.dto;

import cl.duoc.santiago_sacro_api.evento.dto.EventoResumenDto;
import cl.duoc.santiago_sacro_api.evento.dto.ExcepcionHorarioDto;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioDto;
import cl.duoc.santiago_sacro_api.recinto.EstadoApertura;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.TipoRecinto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Datos de la ficha de un recinto (por ahora sin galería). */
public record RecintoDetalleDto(
        Long id,
        String nombre,
        TipoRecinto tipo,
        String comuna,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        String descripcionHistorica,
        EstadoApertura estado,
        LocalDateTime fechaEstado,
        List<HorarioDto> horarios,
        List<ExcepcionHorarioDto> excepciones,
        List<EventoResumenDto> proximosEventos) {

    public static RecintoDetalleDto desde(Recinto r, List<HorarioDto> horarios,
                                          List<ExcepcionHorarioDto> excepciones,
                                          List<EventoResumenDto> proximosEventos) {
        return new RecintoDetalleDto(
                r.getId(), r.getNombreOficial(), r.getTipoRecinto(), r.getComuna().getNombre(),
                r.direccionCompleta(), r.getLatitud(), r.getLongitud(), r.getDescripcionHistorica(),
                r.getEstadoApertura(), r.getFechaEstado(), horarios, excepciones, proximosEventos);
    }
}