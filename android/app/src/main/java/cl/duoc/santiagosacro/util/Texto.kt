package cl.duoc.santiagosacro.util

import java.text.Normalizer

// Quita tildes y diéresis para comparar textos: "Maipú" -> "Maipu", "Ñuñoa" -> "Nunoa".
// NFD separa cada letra de su tilde (ú = u + ´) y la expresión regular borra las tildes.
fun String.sinAcentos(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
