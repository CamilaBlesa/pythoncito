package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.IntentoDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Intento;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import ar.edu.unsam.programacion3.pythoncito.repositories.EjercicioRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.IntentoRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntentoServiceTest {

    @Mock
    private IntentoRepository intentoRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NivelService nivelService;

    @InjectMocks
    private IntentoService intentoService;

    private Usuario estudiante;
    private Ejercicio ejercicio;
    private Nivel nivel;

    @BeforeEach
    void setUp() {
        estudiante = new Usuario();
        ReflectionTestUtils.setField(estudiante, "id", 10L);
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);

        nivel = new Nivel();
        ReflectionTestUtils.setField(nivel, "id", 1L);

        ejercicio = new Ejercicio();
        ReflectionTestUtils.setField(ejercicio, "id", 100L);
        ejercicio.setNivel(nivel);
    }

    @Test
    void registrarIntento_primerIntento_asignaNumeroUno() {
        ArgumentCaptor<Intento> captor = ArgumentCaptor.forClass(Intento.class);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(ejercicioRepository.findById(100L)).thenReturn(Optional.of(ejercicio));
        when(intentoRepository.findTopByUsuarioIdAndEjercicioIdOrderByNumeroIntentoDesc(10L, 100L)).thenReturn(null);
        when(intentoRepository.save(captor.capture())).thenAnswer(invocation -> {
            Intento i = invocation.getArgument(0);
            ReflectionTestUtils.setField(i, "id", 50L);
            return i;
        });

        IntentoDTO resultado = intentoService.registrarIntento(10L, 100L, true);

        assertNotNull(resultado);
        assertEquals(50L, resultado.getId());
        assertEquals(100L, resultado.getEjercicioId());
        assertEquals(1, resultado.getNumeroIntento());
        assertTrue(resultado.getCorrecta());

        Intento guardado = captor.getValue();
        assertEquals(estudiante, guardado.getUsuario());
        assertEquals(ejercicio, guardado.getEjercicio());
        assertEquals(1, guardado.getNumeroIntento());
        assertTrue(guardado.getCorrecta());
        assertNotNull(guardado.getFechaHora());
    }

    @Test
    void registrarIntento_intentoPrevioExistente_incrementaNumero() {
        ArgumentCaptor<Intento> captor = ArgumentCaptor.forClass(Intento.class);
        Intento previo = new Intento();
        previo.setNumeroIntento(3);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(ejercicioRepository.findById(100L)).thenReturn(Optional.of(ejercicio));
        when(intentoRepository.findTopByUsuarioIdAndEjercicioIdOrderByNumeroIntentoDesc(10L, 100L)).thenReturn(previo);
        when(intentoRepository.save(captor.capture())).thenAnswer(invocation -> {
            Intento i = invocation.getArgument(0);
            ReflectionTestUtils.setField(i, "id", 51L);
            return i;
        });

        IntentoDTO resultado = intentoService.registrarIntento(10L, 100L, false);

        assertNotNull(resultado);
        assertEquals(4, resultado.getNumeroIntento());
        assertFalse(resultado.getCorrecta());

        Intento guardado = captor.getValue();
        assertEquals(4, guardado.getNumeroIntento());
        assertFalse(guardado.getCorrecta());
        assertEquals(estudiante, guardado.getUsuario());
    }

    @Test
    void registrarIntento_estudianteNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                intentoService.registrarIntento(99L, 100L, true));
    }

    @Test
    void registrarIntento_rolNoEstudiante_lanzaCredencialInvalida() {
        Usuario admin = new Usuario();
        ReflectionTestUtils.setField(admin, "id", 1L);
        admin.setRol(Usuario.Rol.ADMIN);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(CredencialInvalidaException.class, () ->
                intentoService.registrarIntento(1L, 100L, true));
    }

    @Test
    void registrarIntento_ejercicioNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(ejercicioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                intentoService.registrarIntento(10L, 999L, true));
    }

    @Test
    void listarPorEjercicio_retornaListaIntentosDTO() {
        Intento intento1 = new Intento();
        ReflectionTestUtils.setField(intento1, "id", 1L);
        intento1.setEjercicio(ejercicio);
        intento1.setNumeroIntento(1);
        intento1.setCorrecta(false);
        intento1.setFechaHora(LocalDateTime.now());

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(ejercicioRepository.findById(100L)).thenReturn(Optional.of(ejercicio));
        when(intentoRepository.findByUsuarioIdAndEjercicioIdOrderByNumeroIntentoAsc(10L, 100L))
                .thenReturn(List.of(intento1));

        List<IntentoDTO> resultado = intentoService.listarPorEjercicio(10L, 100L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(nivelService).obtenerNivelParaEstudiante(1L, 10L);
    }

    @Test
    void contarIntentosFallidos_retornaCantidad() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(ejercicioRepository.findById(100L)).thenReturn(Optional.of(ejercicio));
        when(intentoRepository.countByUsuarioIdAndEjercicioIdAndCorrectaFalse(10L, 100L)).thenReturn(3L);

        long fallidos = intentoService.contarIntentosFallidos(10L, 100L);

        assertEquals(3L, fallidos);
    }
}
