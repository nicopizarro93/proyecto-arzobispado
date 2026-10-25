package cl.duoc.aurasacrapp_api.auth;

import java.time.Instant;

/** Token recién creado y su fecha de vencimiento. */
public record TokenGenerado(String valor, Instant expiraEn) {
}
