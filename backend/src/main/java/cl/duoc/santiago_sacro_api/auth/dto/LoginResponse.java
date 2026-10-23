package cl.duoc.santiago_sacro_api.auth.dto;

import cl.duoc.santiago_sacro_api.usuario.dto.UsuarioDto;

import java.time.Instant;

/** Respuesta del login: el token, su tipo, cuándo vence y los datos del usuario. */
public record LoginResponse(String token, String tipo, Instant expiraEn, UsuarioDto usuario) {
}
