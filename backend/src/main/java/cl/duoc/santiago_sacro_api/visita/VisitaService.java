package cl.duoc.santiago_sacro_api.visita;

import cl.duoc.santiago_sacro_api.auth.CuentaDeshabilitadaException;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.RecintoRepository;
import cl.duoc.santiago_sacro_api.ruta.Ruta;
import cl.duoc.santiago_sacro_api.ruta.RutaRepository;
import cl.duoc.santiago_sacro_api.usuario.Rol;
import cl.duoc.santiago_sacro_api.usuario.Usuario;
import cl.duoc.santiago_sacro_api.usuario.UsuarioRepository;
import cl.duoc.santiago_sacro_api.visita.dto.VisitaDto;
import cl.duoc.santiago_sacro_api.visita.dto.VisitaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecintoRepository recintoRepository;
    private final RutaRepository rutaRepository;

    public VisitaService(VisitaRepository visitaRepository, UsuarioRepository usuarioRepository,
                         RecintoRepository recintoRepository, RutaRepository rutaRepository) {
        this.visitaRepository = visitaRepository;
        this.usuarioRepository = usuarioRepository;
        this.recintoRepository = recintoRepository;
        this.rutaRepository = rutaRepository;
    }

    // HU-21 (visitante) y HU-22 (docente): la visita queda AGENDADA.
    @Transactional
    public VisitaDto agendar(Long idUsuario, VisitaRequest datos) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(CuentaDeshabilitadaException::new);
        validarCurso(usuario, datos.nivelCurso());

        Visita visita = new Visita();
        visita.setUsuario(usuario);
        if (datos.idRecinto() != null) {
            visita.setRecinto(recintoActivo(datos.idRecinto()));
        } else {
            visita.setRuta(rutaVisible(datos.idRuta(), idUsuario));
        }
        visita.setInicio(datos.inicio());
        visita.setCantidadPersonas(datos.cantidadPersonas());
        visita.setNivelCurso(datos.nivelCurso());
        visita.setObservaciones(datos.observaciones());
        return VisitaDto.desde(visitaRepository.save(visita));
    }

    // HU-22: en una salida pedagógica el docente indica el curso; un visitante no.
    private void validarCurso(Usuario usuario, String nivelCurso) {
        if (usuario.getRol() == Rol.DOCENTE && nivelCurso == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El curso o nivel es obligatorio en una salida pedagógica");
        }
        if (usuario.getRol() != Rol.DOCENTE && nivelCurso != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo los docentes indican curso o nivel");
        }
    }

    private Recinto recintoActivo(Long idRecinto) {
        return recintoRepository.buscarActivoPorId(idRecinto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un recinto activo con id " + idRecinto));
    }

    // Se puede agendar una ruta sugerida activa o una ruta propia.
    private Ruta rutaVisible(Long idRuta, Long idUsuario) {
        return rutaRepository.buscarSugeridaActiva(idRuta)
                .or(() -> rutaRepository.buscarPersonalActiva(idRuta, idUsuario))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una ruta disponible con id " + idRuta));
    }
}