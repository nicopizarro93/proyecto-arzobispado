package cl.duoc.aurasacrapp_api.ruta;

/** Modo de traslado entre paradas, con su velocidad promedio para estimar tiempos (HU-19). */
public enum ModoTraslado {
    A_PIE(4.5),
    TRANSPORTE_PUBLICO(50),
    VEHICULO(60);

    private final double velocidadKmh;

    ModoTraslado(double velocidadKmh) {
        this.velocidadKmh = velocidadKmh;
    }

    public double velocidadKmh() {
        return velocidadKmh;
    }
}
