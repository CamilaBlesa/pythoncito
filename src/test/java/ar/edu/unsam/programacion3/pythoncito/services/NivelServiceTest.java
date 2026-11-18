package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.ProgresoEstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NivelServiceTest {

    @Mock
    private NivelRepository nivelRepository;

    @Mock
    private ProgresoEstudianteRepository progresoRepository;

    @InjectMocks
    private NivelService nivelService;

    private Nivel nivel1;
    private Nivel nivel2;
    private Nivel nivelInactivo;

    @BeforeEach
    void setUp() {
        nivel1 = new Nivel();
        ReflectionTestUtils.setField(nivel1, "id", 1L);
        nivel1.setTitulo("Nivel 1");
        nivel1.setTema("Variables");
        nivel1.setActivo(true);
        nivel1.setOrdenPresentacion(2);

        nivel2 = new Nivel();
        ReflectionTestUtils.setField(nivel2, "id", 2L);
        nivel2.setTitulo("Nivel 2");
        nivel2.setTema("Condicionales");
        nivel2.setActivo(true);
        nivel2.setOrdenPresentacion(1);

        nivelInactivo = new Nivel();
        ReflectionTestUtils.setField(nivelInactivo, "id", 3L);
        nivelInactivo.setTitulo("Nivel Inactivo");
        nivelInactivo.setTema("Funciones");
        nivelInactivo.setActivo(false);
        nivelInactivo.setOrdenPresentacion(3);
    }

    @Test
    void listar_retornaTodosLosNiveles() {
        when(nivelRepository.findAll()).thenReturn(List.of(nivel1, nivel2));

        List<NivelDTO> resultado = nivelService.listar();

        assertEquals(2, resultado.size());
        verify(nivelRepository).findAll();
    }

    @Test
    void listarActivos_ordenaPorOrdenPresentacionYFiltraInactivos() {
        Nivel nivelSinOrden = new Nivel();
        ReflectionTestUtils.setField(nivelSinOrden, "id", 4L);
        nivelSinOrden.setTitulo("Nivel Sin Orden");
        nivelSinOrden.setActivo(true);
        nivelSinOrden.setOrdenPresentacion(null);

        List<Nivel> todos = new ArrayList<>(List.of(nivel1, nivel2, nivelInactivo, nivelSinOrden));
        when(nivelRepository.findAll()).thenReturn(todos);

        List<NivelDTO> resultado = nivelService.listarActivos();

        assertEquals(3, resultado.size());
        assertEquals(4L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        assertEquals(1L, resultado.get(2).getId());
    }

    @Test
    void obtenerPorId_existente_retornaNivel() {
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel1));

        NivelDTO resultado = nivelService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Nivel 1", resultado.getTitulo());
    }

    @Test
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(nivelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                nivelService.obtenerPorId(99L));
    }

    @Test
    void crear_asignaActivoPorDefectoYGuarda() {
        NivelDTO input = new NivelDTO();
        input.setTitulo("Nuevo");
        input.setTema("Tema");
        input.setOrdenPresentacion(5);

        when(nivelRepository.save(any(Nivel.class))).thenAnswer(invocation -> {
            Nivel n = invocation.getArgument(0);
            ReflectionTestUtils.setField(n, "id", 10L);
            return n;
        });

        NivelDTO resultado = nivelService.crear(input);

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        assertEquals("Nuevo", resultado.getTitulo());
        verify(nivelRepository).save(any(Nivel.class));
    }

    @Test
    void crear_conActivoFalso_respetaValor() {
        NivelDTO input = new NivelDTO();
        input.setTitulo("Nuevo Inactivo");
        input.setActivo(false);

        when(nivelRepository.save(any(Nivel.class))).thenAnswer(invocation -> {
            Nivel n = invocation.getArgument(0);
            ReflectionTestUtils.setField(n, "id", 11L);
            return n;
        });

        NivelDTO resultado = nivelService.crear(input);

        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
    }

    @Test
    void modificar_nivelExistente_actualizaDatos() {
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel1));
        when(nivelRepository.save(any(Nivel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NivelDTO modificacion = new NivelDTO();
        modificacion.setTitulo("Titulo Editado");
        modificacion.setTema("Tema Editado");
        modificacion.setOrdenPresentacion(8);
        modificacion.setActivo(false);

        NivelDTO resultado = nivelService.modificar(1L, modificacion);

        assertEquals("Titulo Editado", resultado.getTitulo());
        assertEquals("Tema Editado", resultado.getTema());
        assertEquals(8, resultado.getOrdenPresentacion());
        assertFalse(resultado.getActivo());
        verify(nivelRepository).save(nivel1);
    }

    @Test
    void modificar_inexistente_lanzaExcepcion() {
        when(nivelRepository.findById(99L)).thenReturn(Optional.empty());

        NivelDTO modificacion = new NivelDTO();
        assertThrows(RecursoNoEncontradoException.class, () ->
                nivelService.modificar(99L, modificacion));
    }

    @Test
    void darDeBaja_nivelExistente_cambiaActivoAFalso() {
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel1));
        when(nivelRepository.save(any(Nivel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NivelDTO resultado = nivelService.darDeBaja(1L);

        assertFalse(resultado.getActivo());
        verify(nivelRepository).save(nivel1);
    }

    @Test
    void darDeBaja_nivelInexistente_lanzaExcepcion() {
        when(nivelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                nivelService.darDeBaja(99L));
    }

    @Test
    void listarActivosParaEstudiante_calculaBloqueadosYCompletados() {
        List<Nivel> todos = new ArrayList<>(List.of(nivel1, nivel2, nivelInactivo));
        when(nivelRepository.findAll()).thenReturn(todos);
        when(progresoRepository.existsByUsuarioIdAndNivelId(10L, 2L)).thenReturn(true);
        when(progresoRepository.existsByUsuarioIdAndNivelId(10L, 1L)).thenReturn(false);

        List<NivelDTO> resultado = nivelService.listarActivosParaEstudiante(10L);

        assertEquals(2, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
        assertTrue(resultado.get(0).isDesbloqueado());
        assertTrue(resultado.get(0).isCompletado());

        assertEquals(1L, resultado.get(1).getId());
        assertTrue(resultado.get(1).isDesbloqueado());
        assertFalse(resultado.get(1).isCompletado());
    }

    @Test
    void obtenerNivelParaEstudiante_desbloqueado_retornaNivel() {
        List<Nivel> todos = new ArrayList<>(List.of(nivel1, nivel2));
        when(nivelRepository.findAll()).thenReturn(todos);
        when(progresoRepository.existsByUsuarioIdAndNivelId(10L, 2L)).thenReturn(false);

        NivelDTO resultado = nivelService.obtenerNivelParaEstudiante(2L, 10L);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertTrue(resultado.isDesbloqueado());
    }

    @Test
    void obtenerNivelParaEstudiante_bloqueado_lanzaForbidden() {
        List<Nivel> todos = new ArrayList<>(List.of(nivel1, nivel2));
        when(nivelRepository.findAll()).thenReturn(todos);
        when(progresoRepository.existsByUsuarioIdAndNivelId(10L, 2L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                nivelService.obtenerNivelParaEstudiante(1L, 10L));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void obtenerNivelParaEstudiante_inexistente_lanzaRecursoNoEncontrado() {
        List<Nivel> todos = new ArrayList<>(List.of(nivel1, nivel2));
        when(nivelRepository.findAll()).thenReturn(todos);

        assertThrows(RecursoNoEncontradoException.class, () ->
                nivelService.obtenerNivelParaEstudiante(99L, 10L));
    }
}
