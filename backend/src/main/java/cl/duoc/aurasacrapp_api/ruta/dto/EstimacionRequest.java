package cl.duoc.aurasacrapp_api.ruta.dto;

import cl.duoc.aurasacrapp_api.ruta.ModoTraslado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


/** Paradas y modo de traslado para calcular el tiempo de una ruta sin guardarla (HU-19). */
public record EstimacionRequest(
        @NotNull(message = "El modo de traslado es obligatorio") ModoTraslado modo,
        @NotNull(message = "Las paradas son obligatorias")
        @Size(min = 2, max = 20, message = "La ruta debe tener entre 2 y 20 paradas")
        List<@Valid @NotNull ParadaRequest> paradas) {

    // HU-18: una ruta no repite recintos.
    @AssertTrue(message = "La ruta no puede repetir recintos")
    public boolean isSinRecintosRepetidos() {
        return ParadaRequest.sinRecintosRepetidos(paradas);
    }
}