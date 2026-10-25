package cl.duoc.aurasacrapp.util

// Formato de los códigos QR de AuraSacrapp: "aurasacrapp:recinto:{id}"
// Ejemplo: el QR de la Capilla del Buen Pastor contiene "aurasacrapp:recinto:1"
object CodigoQr {

    private const val PREFIJO = "aurasacrapp:recinto:"

    // Lee el id del recinto desde el texto del QR. Si el código no es de la app, devuelve null.
    fun leerRecintoId(texto: String?): Long? {
        val limpio = texto?.trim() ?: return null
        if (!limpio.startsWith(PREFIJO, ignoreCase = true)) return null
        val id = limpio.substring(PREFIJO.length).toLongOrNull() ?: return null
        return if (id > 0) id else null
    }

    // Arma el texto que va dentro del QR de un recinto
    fun crear(recintoId: Long): String = "$PREFIJO$recintoId"
}
