package cl.duoc.santiago_sacro_api.ruta;

import cl.duoc.santiago_sacro_api.recinto.EstadoApertura;
import cl.duoc.santiago_sacro_api.recinto.Recinto;
import cl.duoc.santiago_sacro_api.recinto.RecintoRepository;
import cl.duoc.santiago_sacro_api.ruta.dto.EstimacionRequest;
import cl.duoc.santiago_sacro_api.ruta.dto.ParadaDto;
import cl.duoc.santiago_sacro_api.ruta.dto.ParadaRequest;
import cl.duoc.santiago_sacro_api.ruta.dto.RutaDetalleDto;
import cl.duoc.santiago_sacro_api.ruta.dto.RutaRequest;
import cl.duoc.santiago_sacro_api.ruta.dto.RutaResumenDto;
import cl.duoc.santiago_sacro_api.usuario.Usuario;
import cl.duoc.santiago_sacro_api.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RutaServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private RutaService rutaService;
    private Recinto capilla;
    private Recinto sanLucas;
    private Recinto museo;

    @BeforeEach
    void preparar() {
        // La calculadora es real: no usa la base de datos, así que no hace falta simularla.
        rutaService = new RutaService(rutaRepository, recintoRepository, usuarioRepository, new CalculadoraTiempos());
        capilla = recinto(1L, "Capilla del Buen Pastor", "-33.509700", "-70.757400");
        sanLucas = recinto(2L, "Templo San Lucas del Llano", "-33.511200", "-70.752900");
        museo = recinto(3L, "Museo de Arte Sacro Los Aromos", "-33.512600", "-70.750100");
    }

    private Recinto recinto(Long id, String nombre, String latitud, String longitud) {
        Recinto r = new Recinto();
        r.setId(id);
        r.setNombreOficial(nombre);
        r.setLatitud(new BigDecimal(latitud));
        r.setLongitud(new BigDecimal(longitud));
        r.setEstadoApertura(EstadoApertura.ABIERTO);
        return r;
    }

    private Ruta rutaPersonal(Long id, Recinto... recintos) {
        Ruta ruta = new Ruta();
        ruta.setId(id);
        ruta.setTipo(TipoRuta.PERSONALIZADA);
        ruta.setNombre("Mi ruta");
        for (Recinto r : recintos) {
            ruta.agregarParada(r, 30);
        }
        return ruta;
    }

    private RutaRequest solicitud(ModoTraslado modo, Long... idsRecintos) {
        List<ParadaRequest> paradas = Arrays.stream(idsRecintos)
                .map(id -> new ParadaRequest(id, null))
                .toList();
        return new RutaRequest("Mi ruta", null, modo, paradas);
    }

    @Test
    void lasSugeridasTraenSuTiempoTotal() {
        Ruta llano = new Ruta();
        llano.setId(1L);
        llano.setNombre("Ruta del Llano de Maipú");
        llano.setTipo(TipoRuta.SUGERIDA);
        llano.agregarParada(capilla, 30);
        llano.agregarParada(sanLucas, 30);
        llano.agregarParada(museo, 60);
        when(rutaRepository.buscarSugeridasActivas()).thenReturn(List.of(llano));

        RutaResumenDto resumen = rutaService.sugeridas().get(0);

        assertEquals(3, resumen.cantidadParadas());
        assertEquals(134, resumen.minutosTotal());
    }

    @Test
    void estimarConUnRecintoDadoDeBajaResponde404() {
        when(recintoRepository.findByIdInAndActivoTrue(any())).thenReturn(List.of(capilla));
        EstimacionRequest datos = new EstimacionRequest(ModoTraslado.A_PIE,
                List.of(new ParadaRequest(1L, null), new ParadaRequest(8L, null)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> rutaService.estimar(datos));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void crearGuardaUnaRutaPersonalizadaDelUsuarioConLasParadasEnOrden() {
        Usuario marta = new Usuario();
        marta.setId(7L);
        when(usuarioRepository.getReferenceById(7L)).thenReturn(marta);
        when(recintoRepository.findByIdInAndActivoTrue(any())).thenReturn(List.of(capilla, museo));
        when(rutaRepository.save(any(Ruta.class))).thenAnswer(invocacion -> {
            Ruta r = invocacion.getArgument(0);
            r.setId(10L);
            return r;
        });

        RutaDetalleDto creada = rutaService.crear(7L, solicitud(ModoTraslado.A_PIE, 3L, 1L));

        ArgumentCaptor<Ruta> guardada = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaRepository).save(guardada.capture());
        assertEquals(TipoRuta.PERSONALIZADA, guardada.getValue().getTipo());
        assertSame(marta, guardada.getValue().getCreador());
        assertEquals(List.of(3L, 1L), creada.paradas().stream().map(ParadaDto::idRecinto).toList());
        assertEquals(List.of(1, 2), creada.paradas().stream().map(ParadaDto::orden).toList());
        assertEquals(60, creada.estimacion().minutosVisita());
    }

    @Test
    void actualizarReemplazaLasParadasEnElNuevoOrden() {
        Ruta ruta = rutaPersonal(5L, capilla, sanLucas);
        when(rutaRepository.buscarPersonalActiva(5L, 7L)).thenReturn(Optional.of(ruta));
        when(recintoRepository.findByIdInAndActivoTrue(any())).thenReturn(List.of(capilla, sanLucas, museo));

        RutaDetalleDto editada = rutaService.actualizar(7L, 5L, solicitud(ModoTraslado.VEHICULO, 3L, 1L, 2L));

        assertEquals(List.of(3L, 1L, 2L), editada.paradas().stream().map(ParadaDto::idRecinto).toList());
        assertEquals(ModoTraslado.VEHICULO, ruta.getModoTraslado());
        verify(rutaRepository).flush();
    }

    @Test
    void laRutaDeOtroUsuarioResponde404() {
        when(rutaRepository.buscarPersonalActiva(5L, 99L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> rutaService.eliminar(99L, 5L));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void eliminarDesactivaLaRutaSinBorrarla() {
        Ruta ruta = rutaPersonal(5L, capilla, sanLucas);
        when(rutaRepository.buscarPersonalActiva(5L, 7L)).thenReturn(Optional.of(ruta));

        rutaService.eliminar(7L, 5L);

        assertFalse(ruta.getActivo());
        verify(rutaRepository, never()).delete(any());
    }
}
