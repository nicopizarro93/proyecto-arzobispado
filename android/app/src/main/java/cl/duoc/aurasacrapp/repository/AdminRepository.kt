package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.model.Encargado
import cl.duoc.aurasacrapp.model.TipoRecinto
import cl.duoc.aurasacrapp.remote.AdminApi
import cl.duoc.aurasacrapp.remote.RetrofitClient
import cl.duoc.aurasacrapp.remote.dto.CuentaActivaRequest
import cl.duoc.aurasacrapp.remote.dto.EncargadoDto
import cl.duoc.aurasacrapp.remote.dto.EncargadoNuevoRequest
import cl.duoc.aurasacrapp.remote.dto.RecintoNuevoRequest

// Lo que hace el administrador de la red: registrar recintos, darlos de baja y gestionar las cuentas
// de los encargados. Después de cambiar un recinto se vuelve a bajar la lista a Room, así Inicio,
// el mapa y los demás se actualizan.
class AdminRepository(
    private val recintoRepositorio: RecintoRepository,
    private val api: AdminApi = RetrofitClient.adminApi
) {

    // ---- Recintos (HU-35) ----

    suspend fun registrarRecinto(
        tipo: TipoRecinto, nombre: String, comunaId: Long, calle: String, numero: String?,
        latitud: Double, longitud: Double, historia: String?
    ) {
        api.registrarRecinto(
            RecintoNuevoRequest(
                tipo = tipo,
                nombre = nombre.trim(),
                idComuna = comunaId,
                calle = calle.trim(),
                numero = numero?.trim()?.ifEmpty { null },
                latitud = latitud,
                longitud = longitud,
                descripcionHistorica = historia?.trim()?.ifEmpty { null }
            )
        )
        actualizarRecintos()
    }

    suspend fun darDeBaja(recintoId: Long) {
        api.darDeBaja(recintoId)
        actualizarRecintos()
    }

    // Si no hay conexión, el cambio ya quedó en el backend y la lista se actualizará al abrir Inicio
    private suspend fun actualizarRecintos() {
        try {
            recintoRepositorio.sincronizar()
        } catch (e: Exception) {
            // se actualizará la próxima vez que se abra Inicio
        }
    }

    // ---- Encargados (HU-34) ----

    suspend fun encargados(): List<Encargado> = api.encargados().map { it.aModelo() }

    suspend fun crearEncargado(nombre: String, apellido: String, correo: String, password: String, recintoId: Long) {
        api.crearEncargado(
            EncargadoNuevoRequest(nombre.trim(), apellido.trim(), correo.trim().lowercase(), password, recintoId)
        )
    }

    suspend fun cambiarActivo(encargadoId: Long, activo: Boolean) {
        api.cambiarActivo(encargadoId, CuentaActivaRequest(activo))
    }

    private fun EncargadoDto.aModelo() = Encargado(id, nombre, apellido, correo, activo, recinto)
}
