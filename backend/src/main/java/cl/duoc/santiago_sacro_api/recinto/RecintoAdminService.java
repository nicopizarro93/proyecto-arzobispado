package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.comuna.Comuna;
import cl.duoc.santiago_sacro_api.comuna.ComunaRepository;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoDetalleDto;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.time.LocalDateTime;

/** Gestión de recintos por el administrador de la red (HU-35). */
@Service
public class RecintoAdminService {

    private final RecintoRepository recintoRepository;
    private final ComunaRepository comunaRepository;
    private final RecintoService recintoService;

    public RecintoAdminService(RecintoRepository recintoRepository, ComunaRepository comunaRepository,
                               RecintoService recintoService) {
        this.recintoRepository = recintoRepository;
        this.comunaRepository = comunaRepository;
        this.recintoService = recintoService;
    }

    // Registra un recinto nuevo. Parte CERRADO hasta que su encargado lo abra.
    @Transactional
    public RecintoDetalleDto registrar(RecintoRequest datos) {
        Comuna comuna = comunaRepository.findById(datos.idComuna())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe la comuna con id " + datos.idComuna()));
        // La base compara sin distinguir mayúsculas ni tildes: "capilla" y "Capilla" son el mismo nombre.
        if (recintoRepository.existsByNombreOficialAndComunaId(datos.nombre(), comuna.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un recinto llamado \"" + datos.nombre() + "\" en " + comuna.getNombre());
        }
        Recinto recinto = new Recinto();
        recinto.setTipoRecinto(datos.tipo());
        recinto.setNombreOficial(datos.nombre());
        recinto.setComuna(comuna);
        recinto.setCalle(datos.calle());
        recinto.setNumeroCalle(datos.numero());
        // La columna guarda 6 decimales (unos 10 cm de precisión).
        recinto.setLatitud(datos.latitud().setScale(6, RoundingMode.HALF_UP));
        recinto.setLongitud(datos.longitud().setScale(6, RoundingMode.HALF_UP));
        recinto.setDescripcionHistorica(datos.descripcionHistorica());
        recinto.setEstadoApertura(EstadoApertura.CERRADO);
        recinto.setFechaEstado(LocalDateTime.now());
        recinto.setActivo(true);
        Recinto guardado = recintoRepository.save(recinto);
        return recintoService.obtener(guardado.getId());
    }

    // Baja lógica: deja de verse en la app, pero su historial queda en la base.
    @Transactional
    public void darDeBaja(Long idRecinto) {
        Recinto recinto = recintoRepository.findById(idRecinto)
                .filter(r -> Boolean.TRUE.equals(r.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un recinto activo con id " + idRecinto));
        recinto.setActivo(false);
    }
}