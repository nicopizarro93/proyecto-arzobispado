package cl.duoc.santiago_sacro_api.recinto;

import cl.duoc.santiago_sacro_api.comuna.Comuna;
import cl.duoc.santiago_sacro_api.comuna.ComunaRepository;
import cl.duoc.santiago_sacro_api.recinto.dto.RecintoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecintoAdminServiceTest {

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private RecintoService recintoService;

    private RecintoAdminService recintoAdminService;
    private Comuna santiago;

    @BeforeEach
    void preparar() {
        recintoAdminService = new RecintoAdminService(recintoRepository, comunaRepository, recintoService);
        santiago = new Comuna();
        santiago.setId(1L);
        santiago.setNombre("Santiago");
    }

    private RecintoRequest solicitud(String nombre) {
        return new RecintoRequest(TipoRecinto.TEMPLO, nombre, 1L, "Calle Prueba", "100",
                new BigDecimal("-33.43781234"), new BigDecimal("-70.65051234"), null);
    }

    private Recinto recinto(boolean activo) {
        Recinto r = new Recinto();
        r.setId(9L);
        r.setActivo(activo);
        return r;
    }

    @Test
    void registrarGuardaElRecintoCerradoActivoYConSeisDecimales() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(santiago));
        when(recintoRepository.existsByNombreOficialAndComunaId("Parroquia de Prueba", 1L)).thenReturn(false);
        when(recintoRepository.save(any(Recinto.class))).thenAnswer(invocacion -> {
            Recinto r = invocacion.getArgument(0);
            r.setId(10L);
            return r;
        });

        recintoAdminService.registrar(solicitud("Parroquia de Prueba"));

        ArgumentCaptor<Recinto> guardado = ArgumentCaptor.forClass(Recinto.class);
        verify(recintoRepository).save(guardado.capture());
        Recinto r = guardado.getValue();
        assertEquals(EstadoApertura.CERRADO, r.getEstadoApertura());
        assertTrue(r.getActivo());
        assertSame(santiago, r.getComuna());
        assertEquals(new BigDecimal("-33.437812"), r.getLatitud());
        verify(recintoService).obtener(10L);
    }

    @Test
    void unNombreRepetidoEnLaComunaResponde409() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.of(santiago));
        when(recintoRepository.existsByNombreOficialAndComunaId("Capilla del Centro", 1L)).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> recintoAdminService.registrar(solicitud("Capilla del Centro")));
        assertEquals(409, error.getStatusCode().value());
        verify(recintoRepository, never()).save(any());
    }

    @Test
    void unaComunaQueNoExisteResponde404() {
        when(comunaRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> recintoAdminService.registrar(solicitud("Parroquia de Prueba")));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void darDeBajaDesactivaElRecintoSinBorrarlo() {
        Recinto activo = recinto(true);
        when(recintoRepository.findById(9L)).thenReturn(Optional.of(activo));

        recintoAdminService.darDeBaja(9L);

        assertFalse(activo.getActivo());
        verify(recintoRepository, never()).delete(any());
    }

    @Test
    void unRecintoYaDadoDeBajaResponde404() {
        when(recintoRepository.findById(9L)).thenReturn(Optional.of(recinto(false)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> recintoAdminService.darDeBaja(9L));
        assertEquals(404, error.getStatusCode().value());
    }
}