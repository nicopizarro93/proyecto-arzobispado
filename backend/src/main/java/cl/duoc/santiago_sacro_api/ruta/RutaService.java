package cl.duoc.santiago_sacro_api.ruta;

import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.RecintoRepository;
import cl.duoc.santiago_sacro_api.ruta.dto.EstimacionDto;
import cl.duoc.santiago_sacro_api.ruta.dto.EstimacionRequest;
import cl.duoc.santiago_sacro_api.ruta.dto.ParadaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private final RecintoRepository recintoRepository;
    private final CalculadoraTiempos calculadora;

    public RutaService(RecintoRepository recintoRepository, CalculadoraTiempos calculadora) {
        this.recintoRepository = recintoRepository;
        this.calculadora = calculadora;
    }

    // HU-19: tiempo estimado de una ruta que la app está armando, sin guardarla.
    @Transactional(readOnly = true)
    public EstimacionDto estimar(EstimacionRequest datos) {
        Map<Long, Recinto> recintos = recintosActivos(datos.paradas());
        List<PuntoRuta> puntos = datos.paradas().stream()
                .map(p -> PuntoRuta.desde(recintos.get(p.idRecinto()), p.tiempoVisitaMin()))
                .toList();
        return calculadora.estimar(datos.modo(), puntos);
    }

    // Trae los recintos de las paradas en una sola consulta. Uno inexistente o dado de baja responde 404.
    private Map<Long, Recinto> recintosActivos(List<ParadaRequest> paradas) {
        List<Long> ids = paradas.stream().map(ParadaRequest::idRecinto).toList();
        Map<Long, Recinto> encontrados = recintoRepository.findByIdInAndActivoTrue(ids).stream()
                .collect(Collectors.toMap(Recinto::getId, Function.identity()));
        ids.stream()
                .filter(id -> !encontrados.containsKey(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "No existe un recinto activo con id " + id);
                });
        return encontrados;
    }
}