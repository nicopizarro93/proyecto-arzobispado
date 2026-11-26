package cl.duoc.aurasacrapp.repository

import android.content.Context
import android.content.SharedPreferences
import cl.duoc.aurasacrapp.model.Rol
import cl.duoc.aurasacrapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Guarda quién inició sesión y su token JWT.
// Es un object (singleton): hay una sola sesión para toda la app.
// La sesión también se guarda en SharedPreferences (nunca la contraseña), para no pedir
// el inicio de sesión cada vez que se abre la app.
object SesionRepository {

    private const val ARCHIVO = "sesion"

    private var preferencias: SharedPreferences? = null

    // _usuario se puede modificar solo aquí adentro; afuera se expone de solo lectura
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // true cuando el backend rechazó el token (venció): la app vuelve a Bienvenida y avisa
    private val _sesionVencida = MutableStateFlow(false)
    val sesionVencida: StateFlow<Boolean> = _sesionVencida.asStateFlow()

    // Token JWT que se envía al backend en cada llamada (null = sin sesión)
    var token: String? = null
        private set

    // Se llama una vez al abrir la app: abre el almacenamiento y recupera la sesión guardada
    fun inicializar(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        preferencias = prefs
        val tokenGuardado = prefs.getString("token", null)
        val rol = prefs.getString("rol", null)?.let { Rol.valueOf(it) }
        if (tokenGuardado != null && rol != null) {
            token = tokenGuardado
            _usuario.value = Usuario(
                id = prefs.getLong("id", 0),
                rol = rol,
                recintoId = if (prefs.contains("recintoId")) prefs.getLong("recintoId", 0) else null,
                nombre = prefs.getString("nombre", "").orEmpty(),
                apellido = prefs.getString("apellido", "").orEmpty(),
                correo = prefs.getString("correo", "").orEmpty(),
                institucion = prefs.getString("institucion", null)
            )
        }
    }

    // Guarda solo el token: se necesita antes de pedir datos protegidos durante el login
    fun guardarToken(nuevoToken: String) {
        token = nuevoToken
    }

    fun iniciar(usuario: Usuario, nuevoToken: String) {
        token = nuevoToken
        _usuario.value = usuario
        preferencias?.edit()
            ?.putString("token", nuevoToken)
            ?.putLong("id", usuario.id)
            ?.putString("rol", usuario.rol.name)
            ?.putString("nombre", usuario.nombre)
            ?.putString("apellido", usuario.apellido)
            ?.putString("correo", usuario.correo)
            ?.putString("institucion", usuario.institucion)
            ?.apply {
                if (usuario.recintoId != null) putLong("recintoId", usuario.recintoId) else remove("recintoId")
            }
            ?.apply()
    }

    // El usuario cambió sus datos (nombre, apellido, institución): se actualizan en memoria y en
    // las preferencias, sin tocar el token
    fun actualizarUsuario(usuario: Usuario) {
        token?.let { iniciar(usuario, it) }
    }

    fun cerrar() {
        token = null
        _usuario.value = null
        preferencias?.edit()?.clear()?.apply()
    }

    // El backend respondió 401 a una llamada con token: la sesión venció
    fun cerrarPorVencimiento() {
        cerrar()
        _sesionVencida.value = true
    }

    fun avisoVencimientoMostrado() {
        _sesionVencida.value = false
    }
}
