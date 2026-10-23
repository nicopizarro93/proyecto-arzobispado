package cl.duoc.santiago_sacro_api.horario;

import cl.duoc.santiago_sacro_api.horario.dto.HorarioDto;
import cl.duoc.santiago_sacro_api.horario.dto.HorarioRequest;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    private static final Long ID_RECINTO = 1L;

    @Mock
    private HorarioRegularRepository horarioRepository;

    private HorarioService horarioService;
    private Recinto capilla;

    @BeforeEach
    void preparar() {
        horarioService = new HorarioService(horarioRepository);
        capilla = new Recinto();
        capilla.setId(ID_RECINTO);
    }

    // Bloque ya guardado: lunes entre las horas indicadas.
    private HorarioRegular bloque(Long id, String apertura, String cierre) {
        HorarioRegular h = new HorarioRegular();
        h.setId(id);
        h.setRecinto(capilla);
        h.setDiaSemana(1);
        h.setHoraApertura(LocalTime.parse(apertura));
        h.setHoraCierre(LocalTime.parse(cierre));
        return h;
    }

    private HorarioRequest lunes(String apertura, String cierre) {
        return new HorarioRequest(1, LocalTime.parse(apertura), LocalTime.parse(cierre));
    }

    @Test
    void crearSinTraslapeGuardaElBloque() {
        when(horarioRepository.findByRecintoIdAndDiaSemana(ID_RECINTO, 1))
                .thenReturn(List.of(bloque(1L, "09:00", "13:00"), bloque(2L, "16:00", "19:00")));
        when(horarioRepository.save(any(HorarioRegular.class))).thenAnswer(invocacion -> {
            HorarioRegular h = invocacion.getArgument(0);
            h.setId(99L);
            return h;
        });

        HorarioDto creado = horarioService.crear(capilla, lunes("19:30", "21:00"));

        assertEquals(99L, creado.id());
        assertEquals("Lunes", creado.dia());
        assertEquals(LocalTime.of(19, 30), creado.horaApertura());
    }

    @Test
    void crearUnBloqueQueSeCruzaLanzaTraslape() {
        when(horarioRepository.findByRecintoIdAndDiaSemana(ID_RECINTO, 1))
                .thenReturn(List.of(bloque(1L, "09:00", "13:00"), bloque(2L, "16:00", "19:00")));

        assertThrows(HorarioTraslapadoException.class,
                () -> horarioService.crear(capilla, lunes("12:00", "17:00")));
        verify(horarioRepository, never()).save(any());
    }

    @Test
    void bloquesSeguidosSePermiten() {
        when(horarioRepository.findByRecintoIdAndDiaSemana(ID_RECINTO, 1))
                .thenReturn(List.of(bloque(1L, "09:00", "13:00")));
        when(horarioRepository.save(any(HorarioRegular.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        assertDoesNotThrow(() -> horarioService.crear(capilla, lunes("13:00", "16:00")));
    }

    @Test
    void editarNoChocaConsigoMismo() {
        HorarioRegular existente = bloque(5L, "09:00", "13:00");
        when(horarioRepository.findByIdAndRecintoId(5L, ID_RECINTO)).thenReturn(Optional.of(existente));
        when(horarioRepository.findByRecintoIdAndDiaSemana(ID_RECINTO, 1)).thenReturn(List.of(existente));

        HorarioDto editado = horarioService.actualizar(capilla, 5L, lunes("09:00", "12:00"));

        assertEquals(LocalTime.of(12, 0), editado.horaCierre());
        assertEquals(LocalTime.of(12, 0), existente.getHoraCierre());
    }

    @Test
    void bloqueDeOtroRecintoRespondeNoEncontrado() {
        when(horarioRepository.findByIdAndRecintoId(21L, ID_RECINTO)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> horarioService.eliminar(capilla, 21L));
        assertEquals(404, error.getStatusCode().value());
        verify(horarioRepository, never()).delete(any());
    }
}
