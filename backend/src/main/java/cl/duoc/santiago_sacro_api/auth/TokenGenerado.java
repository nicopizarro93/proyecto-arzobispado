package cl.duoc.santiago_sacro_api.auth;

import java.time.Instant;

/** Token recién creado y su fecha de vencimiento. */
public record TokenGenerado(String valor, Instant expiraEn) {
}
