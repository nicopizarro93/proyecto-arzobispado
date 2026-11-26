package cl.duoc.aurasacrapp_api.admin;

import cl.duoc.aurasacrapp_api.admin.dto.CuentaActivaRequest;
import cl.duoc.aurasacrapp_api.admin.dto.EncargadoDto;
import cl.duoc.aurasacrapp_api.admin.dto.EncargadoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Cuentas de encargados, gestionadas por el administrador (HU-34). La ruta /api/admin/** ya exige rol ADMINISTRADOR. */
@RestController
@RequestMapping("/api/admin/encargados")
public class EncargadoAdminController {

    private final EncargadoAdminService encargadoService;

    public EncargadoAdminController(EncargadoAdminService encargadoService) {
        this.encargadoService = encargadoService;
    }

    // GET /api/admin/encargados → todas las cuentas de encargado
    @GetMapping
    public List<EncargadoDto> listar() {
        return encargadoService.listar();
    }

    // POST /api/admin/encargados → 201 con la cuenta creada
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EncargadoDto crear(@Valid @RequestBody EncargadoRequest solicitud) {
        return encargadoService.crear(solicitud);
    }

    // PATCH /api/admin/encargados/7/activo  con {"activo": false}
    @PatchMapping("/{id}/activo")
    public EncargadoDto cambiarActivo(@PathVariable("id") Long id, @Valid @RequestBody CuentaActivaRequest solicitud) {
        return encargadoService.cambiarActivo(id, solicitud.activo());
    }
}
