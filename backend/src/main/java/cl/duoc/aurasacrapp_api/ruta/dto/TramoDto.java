package cl.duoc.aurasacrapp_api.ruta.dto;

/** Traslado entre dos paradas seguidas. */
public record TramoDto(Long desdeIdRecinto, Long hastaIdRecinto, double distanciaKm, int minutos) {
}
