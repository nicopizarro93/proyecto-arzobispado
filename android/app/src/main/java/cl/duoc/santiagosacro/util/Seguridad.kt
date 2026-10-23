package cl.duoc.santiagosacro.util

import java.security.MessageDigest

// Convierte la contraseña en un hash SHA-256 (64 caracteres hexadecimales).
// Así en la BD nunca queda la contraseña en texto plano.
fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}