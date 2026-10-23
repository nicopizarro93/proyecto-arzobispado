package cl.duoc.santiago_sacro_api.ruta.dto;

import cl.duoc.santiago_sacro_api.ruta.ModoTraslado;
import cl.duoc.santiago_sacro_api.ruta.Ruta;

/** Ruta para la lista: nombre, cantidad de paradas y tiempo estimado (HU-17). */
public record RutaResumenDto(Long id,
                             String nombre,
                             String descripcion,
                             ModoTraslado modo,
                             int cantidadParadas,
                             int minutosTotal,
                             double distanciaKm) {

    public static RutaResumenDto desde(Ruta r, EstimacionDto estimacion) {
        return new RutaResumenDto(r.getId(), r.getNombre(), r.getDescripcion(), r.getModoTraslado(),
                r.getParadas().size(), estimacion.minutosTotal(), estimacion.distanciaKm());
    }
}
