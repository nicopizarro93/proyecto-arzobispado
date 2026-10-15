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
import cl.duoc.santiago_sacro_api.ruta.dto.RutaDetalleDto;
import cl.duoc.santiago_sacro_api.ruta.dto.RutaResumenDto;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final RecintoRepository recintoRepository;
    private final CalculadoraTiempos calculadora;

    public RutaService(RutaRepository rutaRepository, RecintoRepository recintoRepository,
                       CalculadoraTiempos calculadora) {
        this.rutaRepository = rutaRepository;
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

    // HU-17: rutas sugeridas activas, cada una con su tiempo total.
    @Transactional(readOnly = true)
    public List<RutaResumenDto> sugeridas() {
        return rutaRepository.buscarSugeridasActivas().stream()
                .map(r -> RutaResumenDto.desde(r, estimacionDe(r)))
                .toList();
    }

    @Transactional(readOnly = true)
    public RutaDetalleDto sugerida(Long id) {
        Ruta ruta = rutaRepository.buscarSugeridaActiva(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una ruta sugerida activa con id " + id));
        return RutaDetalleDto.desde(ruta, estimacionDe(ruta));
    }

    // Tiempo estimado de una ruta guardada, con sus paradas en orden.
    private EstimacionDto estimacionDe(Ruta ruta) {
        List<PuntoRuta> puntos = ruta.getParadas().stream()
                .map(p -> PuntoRuta.desde(p.getRecinto(), p.getTiempoVisitaMin()))
                .toList();
        return calculadora.estimar(ruta.getModoTraslado(), puntos);
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