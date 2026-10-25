package cl.duoc.aurasacrapp_api.auth.dto;

import cl.duoc.aurasacrapp_api.usuario.dto.UsuarioDto;

import java.time.Instant;

/** Respuesta del login: el token, su tipo, cuándo vence y los datos del usuario. */
public record LoginResponse(String token, String tipo, Instant expiraEn, UsuarioDto usuario) {
}
