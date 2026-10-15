package cl.duoc.santiago_sacro_api.ruta.dto;

import cl.duoc.santiago_sacro_api.ruta.ModoTraslado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/** Paradas y modo de traslado para calcular el tiempo de una ruta sin guardarla (HU-19). */
public record EstimacionRequest(
        @NotNull(message = "El modo de traslado es obligatorio") ModoTraslado modo,
        @NotNull(message = "Las paradas son obligatorias")
        @Size(min = 2, max = 20, message = "La ruta debe tener entre 2 y 20 paradas")
        List<@Valid @NotNull ParadaRequest> paradas) {

    // HU-18: una ruta no repite recintos.
    @AssertTrue(message = "La ruta no puede repetir recintos")
    public boolean isSinRecintosRepetidos() {
        if (paradas == null) {
            return true;
        }
        List<Long> ids = paradas.stream()
                .filter(Objects::nonNull)
                .map(ParadaRequest::idRecinto)
                .filter(Objects::nonNull)
                .toList();
        return ids.size() == new HashSet<>(ids).size();
    }
}