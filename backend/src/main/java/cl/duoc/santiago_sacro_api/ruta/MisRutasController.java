package cl.duoc.santiago_sacro_api.ruta;

import cl.duoc.santiago_sacro_api.ruta.dto.RutaDetalleDto;
import cl.duoc.santiago_sacro_api.ruta.dto.RutaRequest;
import cl.duoc.santiago_sacro_api.ruta.dto.RutaResumenDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Rutas propias del usuario con sesión iniciada (HU-18). */
@RestController
@RequestMapping("/api/rutas/mias")
public class MisRutasController {

    private final RutaService rutaService;

    public MisRutasController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    // GET /api/rutas/mias
    @GetMapping
    public List<RutaResumenDto> misRutas(@AuthenticationPrincipal Jwt jwt) {
        return rutaService.misRutas(idUsuario(jwt));
    }

    // GET /api/rutas/mias/5
    @GetMapping("/{id}")
    public RutaDetalleDto miRuta(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        return rutaService.miRuta(idUsuario(jwt), id);
    }

    // POST /api/rutas/mias → 201 con la ruta guardada y su tiempo estimado
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RutaDetalleDto crear(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody RutaRequest solicitud) {
        return rutaService.crear(idUsuario(jwt), solicitud);
    }

    // PUT /api/rutas/mias/5 → reemplaza nombre, modo y paradas
    @PutMapping("/{id}")
    public RutaDetalleDto actualizar(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id,
                                     @Valid @RequestBody RutaRequest solicitud) {
        return rutaService.actualizar(idUsuario(jwt), id, solicitud);
    }

    // DELETE /api/rutas/mias/5 → 204 (la ruta queda desactivada)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        rutaService.eliminar(idUsuario(jwt), id);
    }

    private Long idUsuario(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}