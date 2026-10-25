package cl.duoc.aurasacrapp_api.comuna.dto;

import cl.duoc.aurasacrapp_api.comuna.Comuna;

public record ComunaDto(Long id, String nombre) {

    public static ComunaDto desde(Comuna c) {
        return new ComunaDto(c.getId(), c.getNombre());
    }
}