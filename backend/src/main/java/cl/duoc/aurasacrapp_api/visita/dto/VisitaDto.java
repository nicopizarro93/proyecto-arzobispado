package cl.duoc.aurasacrapp_api.visita.dto;

import cl.duoc.aurasacrapp_api.visita.EstadoVisita;
import cl.duoc.aurasacrapp_api.visita.Visita;

import java.time.LocalDateTime;

/** Una visita de "Mis visitas": destino (recinto o ruta), fecha, personas, curso y estado (HU-25). */
public record VisitaDto(Long id,
                        Long idRecinto,
                        Long idRuta,
                        String destino,
                        LocalDateTime inicio,
                        int cantidadPersonas,
                        String nivelCurso,
                        String observaciones,
                        EstadoVisita estado) {

    public static VisitaDto desde(Visita v) {
        boolean esRecinto = v.getRecinto() != null;
        return new VisitaDto(
                v.getId(),
                esRecinto ? v.getRecinto().getId() : null,
                esRecinto ? null : v.getRuta().getId(),
                esRecinto ? v.getRecinto().getNombreOficial() : v.getRuta().getNombre(),
                v.getInicio(), v.getCantidadPersonas(), v.getNivelCurso(), v.getObservaciones(), v.getEstado());
    }
}
