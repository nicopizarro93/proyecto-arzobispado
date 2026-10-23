package cl.duoc.santiagosacro.model

// Modo de traslado entre paradas, con su velocidad promedio (las mismas del backend)
enum class ModoTraslado(val velocidadKmh: Double) {
    A_PIE(4.5),
    TRANSPORTE_PUBLICO(50.0),
    VEHICULO(60.0)
}
