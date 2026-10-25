package cl.duoc.aurasacrapp.repository

import cl.duoc.aurasacrapp.model.ModoTraslado
import cl.duoc.aurasacrapp.model.ParadaRuta
import cl.duoc.aurasacrapp.model.Ruta

// Rutas sugeridas. En esta entrega son datos fijos, los mismos de la migración V7 del backend.
// En la prueba 3 este repositorio los pedirá a la API sin cambiar las pantallas.
object RutaRepository {

    private data class Lugar(val nombre: String, val comuna: String, val latitud: Double, val longitud: Double)

    // Recintos que aparecen en las rutas (id = el mismo de Room y del backend)
    private val lugares = mapOf(
        1L to Lugar("Capilla del Buen Pastor", "Maipú", -33.509700, -70.757400),
        2L to Lugar("Templo San Lucas del Llano", "Maipú", -33.511200, -70.752900),
        3L to Lugar("Museo de Arte Sacro Los Aromos", "Maipú", -33.512600, -70.750100),
        4L to Lugar("Parroquia Santa Rosa de la Cañada", "Cerrillos", -33.495800, -70.716300),
        5L to Lugar("Archivo Histórico La Chimba", "Recoleta", -33.428900, -70.646200),
        6L to Lugar("Capilla San José Obrero", "Puente Alto", -33.611700, -70.575800),
        7L to Lugar("Colegio San Esteban", "Puente Alto", -33.604500, -70.581200)
    )

    private fun parada(orden: Int, recintoId: Long, minutos: Int): ParadaRuta {
        val lugar = lugares.getValue(recintoId)
        return ParadaRuta(orden, recintoId, lugar.nombre, lugar.comuna, lugar.latitud, lugar.longitud, minutos)
    }

    private val rutas = listOf(
        Ruta(
            id = 1, nombre = "Ruta del Llano de Maipú",
            descripcion = "Tres recintos del antiguo barrio del Llano, a pocas cuadras entre sí.",
            modoTraslado = ModoTraslado.A_PIE,
            paradas = listOf(parada(1, 1, 30), parada(2, 2, 30), parada(3, 3, 60))
        ),
        Ruta(
            id = 2, nombre = "Patrimonio del poniente",
            descripcion = "Del museo de Maipú al archivo de La Chimba, pasando por la parroquia de Cerrillos.",
            modoTraslado = ModoTraslado.TRANSPORTE_PUBLICO,
            paradas = listOf(parada(1, 3, 60), parada(2, 4, 30), parada(3, 5, 45))
        ),
        Ruta(
            id = 3, nombre = "Fe y escuela en Puente Alto",
            descripcion = "Capilla del barrio y colegio con museo escolar.",
            modoTraslado = ModoTraslado.VEHICULO,
            paradas = listOf(parada(1, 6, 30), parada(2, 7, 45))
        )
    )

    fun rutas(): List<Ruta> = rutas

    fun ruta(id: Long): Ruta? = rutas.find { it.id == id }
}
