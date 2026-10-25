package cl.duoc.aurasacrapp_api.ruta;

import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.recinto.RecintoRepository;
import cl.duoc.aurasacrapp_api.ruta.dto.EstimacionDto;
import cl.duoc.aurasacrapp_api.ruta.dto.EstimacionRequest;
import cl.duoc.aurasacrapp_api.ruta.dto.ParadaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.duoc.aurasacrapp_api.ruta.dto.RutaDetalleDto;
import cl.duoc.aurasacrapp_api.ruta.dto.RutaResumenDto;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import cl.duoc.aurasacrapp_api.ruta.dto.RutaRequest;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final RecintoRepository recintoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalculadoraTiempos calculadora;

    public RutaService(RutaRepository rutaRepository, RecintoRepository recintoRepository,
                       UsuarioRepository usuarioRepository, CalculadoraTiempos calculadora) {
        this.rutaRepository = rutaRepository;
        this.recintoRepository = recintoRepository;
        this.usuarioRepository = usuarioRepository;
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

    // ---- Mis rutas (HU-18): solo las del usuario del token ----

    @Transactional(readOnly = true)
    public List<RutaResumenDto> misRutas(Long idUsuario) {
        return rutaRepository.buscarPersonalesActivas(idUsuario).stream()
                .map(r -> RutaResumenDto.desde(r, estimacionDe(r)))
                .toList();
    }

    @Transactional(readOnly = true)
    public RutaDetalleDto miRuta(Long idUsuario, Long idRuta) {
        Ruta ruta = buscarMia(idUsuario, idRuta);
        return RutaDetalleDto.desde(ruta, estimacionDe(ruta));
    }

    @Transactional
    public RutaDetalleDto crear(Long idUsuario, RutaRequest datos) {
        Ruta ruta = new Ruta();
        ruta.setCreador(usuarioRepository.getReferenceById(idUsuario));
        ruta.setTipo(TipoRuta.PERSONALIZADA);
        copiar(datos, ruta);
        Ruta guardada = rutaRepository.save(ruta);
        return RutaDetalleDto.desde(guardada, estimacionDe(guardada));
    }

    @Transactional
    public RutaDetalleDto actualizar(Long idUsuario, Long idRuta, RutaRequest datos) {
        Ruta ruta = buscarMia(idUsuario, idRuta);
        // Primero se borran en la base las paradas antiguas (flush) y después se agregan las nuevas.
        // Si se hiciera todo junto, MySQL vería dos paradas con el mismo orden en la misma ruta.
        ruta.getParadas().clear();
        rutaRepository.flush();
        copiar(datos, ruta);
        return RutaDetalleDto.desde(ruta, estimacionDe(ruta));
    }

    // Borrado lógico: la ruta queda desactivada, no se elimina.
    @Transactional
    public void eliminar(Long idUsuario, Long idRuta) {
        buscarMia(idUsuario, idRuta).setActivo(false);
    }

    // Una ruta de otro usuario responde 404, igual que una que no existe.
    private Ruta buscarMia(Long idUsuario, Long idRuta) {
        return rutaRepository.buscarPersonalActiva(idRuta, idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No tienes una ruta con id " + idRuta));
    }

    private void copiar(RutaRequest datos, Ruta ruta) {
        ruta.setNombre(datos.nombre());
        ruta.setDescripcion(datos.descripcion());
        ruta.setModoTraslado(datos.modo());
        Map<Long, Recinto> recintos = recintosActivos(datos.paradas());
        datos.paradas().forEach(p -> ruta.agregarParada(recintos.get(p.idRecinto()), p.tiempoVisitaMin()));
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