package cl.duoc.aurasacrapp.remote.dto

import cl.duoc.aurasacrapp.model.TipoRecinto

// Datos del panel del administrador tal como viajan entre la app y el backend

// Recinto nuevo para la red (HU-35). Nace "Cerrado" hasta que su encargado lo abra
data class RecintoNuevoRequest(
    val tipo: TipoRecinto,
    val nombre: String,
    val idComuna: Long,
    val calle: String,
    val numero: String?,
    val latitud: Double,
    val longitud: Double,
    val descripcionHistorica: String?
)

// Cuenta de encargado con el recinto que tiene a cargo (nunca incluye la contraseña)
data class EncargadoDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val activo: Boolean,
    val idRecinto: Long?,
    val recinto: String?
)

// Cuenta de encargado nueva (HU-34): la clave inicial la elige el administrador
data class EncargadoNuevoRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val idRecinto: Long
)

data class CuentaActivaRequest(
    val activo: Boolean
)
