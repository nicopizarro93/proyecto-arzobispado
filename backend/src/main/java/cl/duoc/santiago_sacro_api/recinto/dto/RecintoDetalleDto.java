package cl.duoc.santiago_sacro_api.recinto.dto;

import cl.duoc.santiago_sacro_api.recinto.EstadoApertura;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.TipoRecinto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos de la ficha de un recinto (por ahora sin horarios, galería ni eventos). */
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
        LocalDateTime fechaEstado) {

    public static RecintoDetalleDto desde(Recinto r) {
        return new RecintoDetalleDto(
                r.getId(), r.getNombreOficial(), r.getTipoRecinto(), r.getComuna().getNombre(),
                r.direccionCompleta(), r.getLatitud(), r.getLongitud(), r.getDescripcionHistorica(),
                r.getEstadoApertura(), r.getFechaEstado());
    }
}
