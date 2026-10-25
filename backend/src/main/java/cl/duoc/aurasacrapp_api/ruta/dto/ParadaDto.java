package cl.duoc.aurasacrapp_api.ruta.dto;

import cl.duoc.aurasacrapp_api.recinto.EstadoApertura;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.ruta.ParadaRuta;

import java.math.BigDecimal;

/** Parada de una ruta: con coordenadas para el trazado y estado para avisar si está cerrada (HU-20). */
public record ParadaDto(int orden,
                        Long idRecinto,
                        String recinto,
                        EstadoApertura estado,
                        BigDecimal latitud,
                        BigDecimal longitud,
                        int tiempoVisitaMin) {

    public static ParadaDto desde(ParadaRuta p) {
        Recinto r = p.getRecinto();
        return new ParadaDto(p.getOrden(), r.getId(), r.getNombreOficial(), r.getEstadoApertura(),
                r.getLatitud(), r.getLongitud(), p.getTiempoVisitaMin());
    }
}
