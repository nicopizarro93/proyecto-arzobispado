package cl.duoc.santiago_sacro_api.ruta.dto;

import cl.duoc.santiago_sacro_api.ruta.ModoTraslado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Datos para guardar o editar una ruta propia (HU-18). */
public record RutaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres") String nombre,
        @Size(max = 1000, message = "La descripción admite hasta 1000 caracteres") String descripcion,
        @NotNull(message = "El modo de traslado es obligatorio") ModoTraslado modo,
        @NotNull(message = "Las paradas son obligatorias")
        @Size(min = 2, max = 20, message = "La ruta debe tener entre 2 y 20 paradas")
        List<@Valid @NotNull ParadaRequest> paradas) {

    public RutaRequest {
        nombre = nombre == null ? null : nombre.trim();
        descripcion = (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    }

    @AssertTrue(message = "La ruta no puede repetir recintos")
    public boolean isSinRecintosRepetidos() {
        return ParadaRequest.sinRecintosRepetidos(paradas);
    }
}
