package cl.duoc.aurasacrapp_api.recinto;

import cl.duoc.aurasacrapp_api.recinto.dto.RecintoDetalleDto;
import cl.duoc.aurasacrapp_api.recinto.dto.RecintoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Recintos de la red, gestionados por el administrador (HU-35). */
@RestController
@RequestMapping("/api/admin/recintos")
public class RecintoAdminController {

    private final RecintoAdminService recintoAdminService;

    public RecintoAdminController(RecintoAdminService recintoAdminService) {
        this.recintoAdminService = recintoAdminService;
    }

    // POST /api/admin/recintos → 201 con la ficha del recinto nuevo
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecintoDetalleDto registrar(@Valid @RequestBody RecintoRequest solicitud) {
        return recintoAdminService.registrar(solicitud);
    }

    // PATCH /api/admin/recintos/9/baja → 204 (baja lógica)
    @PatchMapping("/{id}/baja")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void darDeBaja(@PathVariable("id") Long id) {
        recintoAdminService.darDeBaja(id);
    }
}
