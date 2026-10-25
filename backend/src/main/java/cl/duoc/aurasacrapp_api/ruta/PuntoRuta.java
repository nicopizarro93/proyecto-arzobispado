package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.recinto.Recinto;

import java.math.BigDecimal;

/** Una parada para el cálculo: dónde está el recinto y cuánto dura la visita. */
public record PuntoRuta(Long idRecinto, BigDecimal latitud, BigDecimal longitud, int tiempoVisitaMin) {

    public static PuntoRuta desde(Recinto r, int tiempoVisitaMin) {
        return new PuntoRuta(r.getId(), r.getLatitud(), r.getLongitud(), tiempoVisitaMin);
    }
}