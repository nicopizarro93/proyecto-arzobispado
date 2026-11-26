package cl.duoc.aurasacrapp_api.guia;

import cl.duoc.aurasacrapp_api.guia.dto.DisponibilidadRequest;
import cl.duoc.aurasacrapp_api.guia.dto.SolicitudRecintoDto;
import cl.duoc.aurasacrapp_api.recinto.Recinto;
import cl.duoc.aurasacrapp_api.usuario.Rol;
import cl.duoc.aurasacrapp_api.usuario.Usuario;
import cl.duoc.aurasacrapp_api.usuario.UsuarioRepository;
import cl.duoc.aurasacrapp_api.visita.EstadoVisita;
import cl.duoc.aurasacrapp_api.visita.Visita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuiaEncargadoServiceTest {

    private static final Long ID_USUARIO = 10L;
    private static final Long ID_SOLICITUD = 5L;
    private static final Long ID_GUIA = 3L;
    private static final LocalDateTime INICIO_VISITA = LocalDateTime.of(2026, 12, 5, 10, 0);

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private GuiaRepository guiaRepository;
    @Mock
    private DisponibilidadGuiaRepository disponibilidadRepository;
    @Mock
    private SolicitudGuiaRepository solicitudRepository;

    private GuiaEncargadoService servicio;
    private Recinto capilla;
    private Guia guia;
    private SolicitudGuia solicitud;

    @BeforeEach
    void preparar() {
        servicio = new GuiaEncargadoService(usuarioRepository, guiaRepository, disponibilidadRepository, solicitudRepository);

        capilla = new Recinto();
        capilla.setId(1L);

        guia = new Guia();
        guia.setId(ID_GUIA);
        guia.setRecinto(capilla);
        guia.setNombre("Marta");
        guia.setApellido("Díaz");

        Visita visita = new Visita();
        visita.setId(7L);
        visita.setInicio(INICIO_VISITA);
        visita.setEstado(EstadoVisita.AGENDADA);
        visita.setCantidadPersonas(30);
        visita.setUsuario(new Usuario());

        solicitud = new SolicitudGuia();
        solicitud.setId(ID_SOLICITUD);
        solicitud.setVisita(visita);
        solicitud.setRecinto(capilla);
    }

    private void cuentaDeEncargado() {
        Usuario encargado = new Usuario();
        encargado.setId(ID_USUARIO);
        encargado.setRol(Rol.ENCARGADO);
        encargado.setActivo(true);
        encargado.setRecinto(capilla);
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(encargado));
    }

    private void solicitudDelRecinto() {
        when(solicitudRepository.buscarUnaDelRecinto(ID_SOLICITUD, 1L)).thenReturn(Optional.of(solicitud));
    }

    private ResponseStatusException errorAlAsignar() {
        return assertThrows(ResponseStatusException.class, () -> servicio.asignar(ID_USUARIO, ID_SOLICITUD, ID_GUIA));
    }

    @Test
    void asignaElGuiaCuandoEstaActivoDisponibleYLibre() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        when(guiaRepository.findByIdAndRecintoId(ID_GUIA, 1L)).thenReturn(Optional.of(guia));
        when(disponibilidadRepository.existsByGuiaIdAndInicioLessThanEqualAndFinGreaterThanEqual(ID_GUIA, INICIO_VISITA, INICIO_VISITA))
                .thenReturn(true);
        when(solicitudRepository.guiaOcupado(ID_GUIA, INICIO_VISITA, ID_SOLICITUD)).thenReturn(false);
        when(usuarioRepository.getReferenceById(ID_USUARIO)).thenReturn(new Usuario());

        SolicitudRecintoDto resultado = servicio.asignar(ID_USUARIO, ID_SOLICITUD, ID_GUIA);

        assertEquals(EstadoSolicitud.ASIGNADA, solicitud.getEstado());
        assertEquals(guia, solicitud.getGuia());
        assertNotNull(solicitud.getFechaResolucion());
        assertEquals("Marta Díaz", resultado.guia());
    }

    @Test
    void noAsignaUnGuiaQueNoEstaDisponibleEnEseHorario() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        when(guiaRepository.findByIdAndRecintoId(ID_GUIA, 1L)).thenReturn(Optional.of(guia));
        when(disponibilidadRepository.existsByGuiaIdAndInicioLessThanEqualAndFinGreaterThanEqual(ID_GUIA, INICIO_VISITA, INICIO_VISITA))
                .thenReturn(false);

        assertEquals(HttpStatus.CONFLICT, errorAlAsignar().getStatusCode());
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertNull(solicitud.getGuia());
    }

    @Test
    void noAsignaUnGuiaQueYaTieneOtraVisitaALaMismaHora() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        when(guiaRepository.findByIdAndRecintoId(ID_GUIA, 1L)).thenReturn(Optional.of(guia));
        when(disponibilidadRepository.existsByGuiaIdAndInicioLessThanEqualAndFinGreaterThanEqual(ID_GUIA, INICIO_VISITA, INICIO_VISITA))
                .thenReturn(true);
        when(solicitudRepository.guiaOcupado(ID_GUIA, INICIO_VISITA, ID_SOLICITUD)).thenReturn(true);

        assertEquals(HttpStatus.CONFLICT, errorAlAsignar().getStatusCode());
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    }

    @Test
    void noAsignaUnGuiaDesactivado() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        guia.setActivo(false);
        when(guiaRepository.findByIdAndRecintoId(ID_GUIA, 1L)).thenReturn(Optional.of(guia));

        assertEquals(HttpStatus.CONFLICT, errorAlAsignar().getStatusCode());
    }

    @Test
    void unGuiaDeOtroRecintoResponde404() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        when(guiaRepository.findByIdAndRecintoId(ID_GUIA, 1L)).thenReturn(Optional.empty());

        assertEquals(HttpStatus.NOT_FOUND, errorAlAsignar().getStatusCode());
    }

    @Test
    void unaSolicitudDeOtroRecintoResponde404() {
        cuentaDeEncargado();
        when(solicitudRepository.buscarUnaDelRecinto(ID_SOLICITUD, 1L)).thenReturn(Optional.empty());

        assertEquals(HttpStatus.NOT_FOUND, errorAlAsignar().getStatusCode());
    }

    @Test
    void noSeResuelveDosVecesUnaSolicitud() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);

        assertEquals(HttpStatus.CONFLICT, errorAlAsignar().getStatusCode());
    }

    @Test
    void rechazarGuardaElMotivoYElEstado() {
        cuentaDeEncargado();
        solicitudDelRecinto();
        when(usuarioRepository.getReferenceById(ID_USUARIO)).thenReturn(new Usuario());

        SolicitudRecintoDto resultado = servicio.rechazar(ID_USUARIO, ID_SOLICITUD, "No hay guías ese día");

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
        assertEquals("No hay guías ese día", resultado.motivoRechazo());
        assertNotNull(solicitud.getFechaResolucion());
    }

    @Test
    void unaCuentaQueNoEsEncargadoRecibeAccesoDenegado() {
        Usuario visitante = new Usuario();
        visitante.setId(ID_USUARIO);
        visitante.setRol(Rol.VISITANTE);
        visitante.setActivo(true);
        when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(visitante));

        assertThrows(AccessDeniedException.class, () -> servicio.guias(ID_USUARIO));
        verifyNoInteractions(guiaRepository);
    }

    @Test
    void noSeAgregaUnBloqueQueSeCruzaConOtroDelMismoGuia() {
        cuentaDeEncargado();
        when(guiaRepository.findByIdAndRecintoId(ID_GUIA, 1L)).thenReturn(Optional.of(guia));
        LocalDateTime inicio = LocalDateTime.of(2026, 12, 5, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 12, 5, 12, 0);
        when(disponibilidadRepository.findByGuiaIdAndInicioLessThanAndFinGreaterThan(ID_GUIA, fin, inicio))
                .thenReturn(List.of(new DisponibilidadGuia()));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> servicio.agregarDisponibilidad(ID_USUARIO, ID_GUIA, new DisponibilidadRequest(inicio, fin)));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(disponibilidadRepository, never()).save(any());
    }
}
