package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.ruta.dto.EstimacionDto;
import cl.duoc.aurasacrapp_api.ruta.dto.TramoDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CalculadoraTiempos {

    // Las calles no van en línea recta: se estima un recorrido 30 % más largo que la línea recta.
    static final double FACTOR_DESVIO = 1.3;
    private static final double RADIO_TIERRA_KM = 6371.0;

    public EstimacionDto estimar(ModoTraslado modo, List<PuntoRuta> puntos) {
        List<TramoDto> tramos = new ArrayList<>();
        for (int i = 0; i < puntos.size() - 1; i++) {
            PuntoRuta desde = puntos.get(i);
            PuntoRuta hasta = puntos.get(i + 1);
            double km = distanciaKm(desde, hasta) * FACTOR_DESVIO;
            // Se redondea hacia arriba: un tramo corto cuenta al menos 1 minuto.
            int minutos = (int) Math.ceil(km / modo.velocidadKmh() * 60);
            tramos.add(new TramoDto(desde.idRecinto(), hasta.idRecinto(), redondear(km), minutos));
        }
        int minutosVisita = puntos.stream().mapToInt(PuntoRuta::tiempoVisitaMin).sum();
        int minutosTraslado = tramos.stream().mapToInt(TramoDto::minutos).sum();
        double distanciaTotal = tramos.stream().mapToDouble(TramoDto::distanciaKm).sum();
        return new EstimacionDto(modo, minutosVisita, minutosTraslado, minutosVisita + minutosTraslado,
                redondear(distanciaTotal), tramos);
    }

    // Fórmula de Haversine: distancia en línea recta sobre la superficie de la Tierra.
    static double distanciaKm(PuntoRuta a, PuntoRuta b) {
        double lat1 = Math.toRadians(a.latitud().doubleValue());
        double lat2 = Math.toRadians(b.latitud().doubleValue());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.longitud().doubleValue() - a.longitud().doubleValue());
        double h = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(h));
    }

    private static double redondear(double km) {
        return Math.round(km * 100) / 100.0;
    }
}
