package cl.duoc.aurasacrapp_api.guia.dto;

import cl.duoc.aurasacrapp_api.guia.Guia;

import java.util.List;

/** Guía con sus bloques de disponibilidad. */
public record GuiaDto(Long id, String nombre, String apellido, boolean activo,
                      List<DisponibilidadDto> disponibilidad) {

    public static GuiaDto desde(Guia g, List<DisponibilidadDto> disponibilidad) {
        return new GuiaDto(g.getId(), g.getNombre(), g.getApellido(), Boolean.TRUE.equals(g.getActivo()), disponibilidad);
    }
}
