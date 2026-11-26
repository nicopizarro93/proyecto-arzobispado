package cl.duoc.aurasacrapp.remote

import android.util.Log
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.CancellationException

// El backend responde los errores en JSON (ProblemDetail): "detail" con el mensaje
// y, en los errores de validación, "errores" con un mensaje por campo
private data class ProblemaApi(
    val detail: String?,
    val errores: Map<String, String>?
)

// Convierte cualquier error de una llamada a la API en un mensaje para el usuario
fun mensajeDeError(error: Throwable): String {
    // Si la pantalla se cerró mientras la llamada seguía en curso, la corrutina se cancela:
    // no es un error, así que se vuelve a lanzar para que Kotlin la termine con normalidad
    if (error is CancellationException) throw error
    // Queda en el Logcat (filtrar por AuraSacrapp) para saber la causa real
    Log.e("AuraSacrapp", "Error al llamar a la API", error)
    return textoDeError(error)
}

private fun textoDeError(error: Throwable): String = when (error) {
    is HttpException -> {
        val cuerpo = error.response()?.errorBody()?.string()
        val problema = try {
            Gson().fromJson(cuerpo, ProblemaApi::class.java)
        } catch (e: Exception) {
            null
        }
        problema?.errores?.values?.firstOrNull()
            ?: problema?.detail
            ?: "Error del servidor (${error.code()})"
    }
    is IOException -> "No se pudo conectar con el servidor"
    else -> "Ocurrió un error inesperado"
}
