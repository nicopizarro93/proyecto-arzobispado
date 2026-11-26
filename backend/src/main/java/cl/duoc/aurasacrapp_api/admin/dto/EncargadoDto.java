package cl.duoc.aurasacrapp_api.admin.dto;

import cl.duoc.aurasacrapp_api.usuario.Usuario;

/** Cuenta de un encargado con el recinto que tiene a cargo (nunca incluye la contraseña). */
public record EncargadoDto(Long id, String nombre, String apellido, String correo, boolean activo,
                           Long idRecinto, String recinto) {

    public static EncargadoDto desde(Usuario u) {
        return new EncargadoDto(
                u.getId(), u.getNombre(), u.getApellido(), u.getCorreo(), Boolean.TRUE.equals(u.getActivo()),
                u.getRecinto() == null ? null : u.getRecinto().getId(),
                u.getRecinto() == null ? null : u.getRecinto().getNombreOficial());
    }
}
