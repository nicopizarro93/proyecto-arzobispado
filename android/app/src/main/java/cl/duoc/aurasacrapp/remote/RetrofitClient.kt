package cl.duoc.aurasacrapp.remote

import cl.duoc.aurasacrapp.repository.SesionRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 es la IP con la que el emulador de Android accede al localhost del PC
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Agrega el token JWT a cada llamada (menos login y registro, que son públicos:
    // el backend rechaza un token vencido incluso en los endpoints públicos)
    private val cliente = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val solicitud = cadena.request()
            val token = SesionRepository.token
            val esAutenticacion = solicitud.url.encodedPath.startsWith("/api/auth/")
            if (token != null && !esAutenticacion) {
                val respuesta = cadena.proceed(solicitud.newBuilder().header("Authorization", "Bearer $token").build())
                // 401 con token = el token venció: se cierra la sesión (la app vuelve a Bienvenida)
                if (respuesta.code == 401) SesionRepository.cerrarPorVencimiento()
                respuesta
            } else {
                cadena.proceed(solicitud)
            }
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val recintoApi: RecintoApi by lazy { retrofit.create(RecintoApi::class.java) }
    val eventoApi: EventoApi by lazy { retrofit.create(EventoApi::class.java) }
    val rutaApi: RutaApi by lazy { retrofit.create(RutaApi::class.java) }
    val visitaApi: VisitaApi by lazy { retrofit.create(VisitaApi::class.java) }
    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val adminApi: AdminApi by lazy { retrofit.create(AdminApi::class.java) }
    val encargadoApi: EncargadoApi by lazy { retrofit.create(EncargadoApi::class.java) }
}
