package cl.duoc.aurasacrapp_api.recinto.dto;

import cl.duoc.aurasacrapp_api.recinto.EstadoApertura;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.TipoRecinto;

import java.math.BigDecimal;

/** Datos de un recinto para la lista y el mapa. */
public record RecintoResumenDto(
        Long id,
        String nombre,
        TipoRecinto tipo,
        String comuna,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        EstadoApertura estado) {

    public static RecintoResumenDto desde(Recinto r) {
        return new RecintoResumenDto(
                r.getId(), r.getNombreOficial(), r.getTipoRecinto(), r.getComuna().getNombre(),
                r.direccionCompleta(), r.getLatitud(), r.getLongitud(), r.getEstadoApertura());
    }
}