package cl.duoc.aurasacrapp_api.comuna;

import cl.duoc.aurasacrapp_api.comuna.dto.ComunaDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComunaServiceTest {

    @Mock
    private ComunaRepository comunaRepository;

    private Comuna comuna(Long id, String nombre) {
        Comuna c = new Comuna();
        c.setId(id);
        c.setNombre(nombre);
        return c;
    }

    @Test
    void listaLasComunasEnElOrdenDelRepositorio() {
        when(comunaRepository.findAllByOrderByNombreAsc())
                .thenReturn(List.of(comuna(2L, "Cerrillos"), comuna(19L, "Maipú")));

        List<ComunaDto> comunas = new ComunaService(comunaRepository).listar();

        assertEquals(List.of(new ComunaDto(2L, "Cerrillos"), new ComunaDto(19L, "Maipú")), comunas);
    }
}
