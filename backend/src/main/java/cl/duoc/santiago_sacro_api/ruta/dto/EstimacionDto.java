package cl.duoc.santiago_sacro_api.ruta.dto;

import cl.duoc.santiago_sacro_api.ruta.ModoTraslado;

import java.util.List;

/** Tiempo estimado de una ruta: visitas + traslados, con el detalle de cada tramo (HU-19). */
public record EstimacionDto(ModoTraslado modo,
                            int minutosVisita,
                            int minutosTraslado,
                            int minutosTotal,
                            double distanciaKm,
                            List<TramoDto> tramos) {
}