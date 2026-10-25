package cl.duoc.aurasacrapp_api.usuario.dto;

import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;

public record UsuarioDto(Long id, String nombre, String apellido, String correo, Rol rol, String institucion) {

    public static UsuarioDto desde(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getRol(), u.getInstitucion());
    }
}
