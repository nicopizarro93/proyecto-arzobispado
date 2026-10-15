package cl.duoc.santiago_sacro_api.ruta.dto;

import cl.duoc.santiago_sacro_api.ruta.ModoTraslado;
import cl.duoc.santiago_sacro_api.ruta.Ruta;

import java.util.List;

/** Detalle de una ruta: paradas en orden y tiempo de cada tramo (HU-17, HU-19). */
public record RutaDetalleDto(Long id,
                             String nombre,
                             String descripcion,
                             ModoTraslado modo,
                             List<ParadaDto> paradas,
                             EstimacionDto estimacion) {

    public static RutaDetalleDto desde(Ruta r, EstimacionDto estimacion) {
        return new RutaDetalleDto(r.getId(), r.getNombre(), r.getDescripcion(), r.getModoTraslado(),
                r.getParadas().stream().map(ParadaDto::desde).toList(), estimacion);
    }
}
