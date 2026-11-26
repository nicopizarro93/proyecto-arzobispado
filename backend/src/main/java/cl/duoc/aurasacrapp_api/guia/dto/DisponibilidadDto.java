package cl.duoc.aurasacrapp_api.guia.dto;

import cl.duoc.aurasacrapp_api.guia.DisponibilidadGuia;

import java.time.LocalDateTime;

public record DisponibilidadDto(Long id, LocalDateTime inicio, LocalDateTime fin) {

    public static DisponibilidadDto desde(DisponibilidadGuia d) {
        return new DisponibilidadDto(d.getId(), d.getInicio(), d.getFin());
    }
}
