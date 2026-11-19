package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.ProgresoEstudianteDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.models.ProgresoEstudiante;
import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import ar.edu.unsam.programacion3.pythoncito.repositories.IntentoRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.ProgresoEstudianteRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgresoEstudianteServiceTest {

    @Mock
    private ProgresoEstudianteRepository progresoRepository;

    @Mock
    private IntentoRepository intentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NivelRepository nivelRepository;

    @InjectMocks
    private ProgresoEstudianteService progresoEstudianteService;

    private Usuario estudiante;
    private Nivel nivel;
    private Ejercicio ejercicio1;

    @BeforeEach
    void setUp() {
        estudiante = new Usuario();
        ReflectionTestUtils.setField(estudiante, "id", 10L);
        estudiante.setUsername("estudiante1");
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);

        nivel = new Nivel();
        ReflectionTestUtils.setField(nivel, "id", 1L);
        nivel.setTitulo("Nivel 1");
        nivel.setTema("Variables");

        ejercicio1 = new Ejercicio();
        ReflectionTestUtils.setField(ejercicio1, "id", 100L);
        ejercicio1.setNivel(nivel);
        nivel.setEjercicios(new ArrayList<>(List.of(ejercicio1)));
    }

    @Test
    void completarNivelSiCorresponde_estudianteInexistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                progresoEstudianteService.completarNivelSiCorresponde(99L, 1L));
    }

    @Test
    void completarNivelSiCorresponde_rolNoEstudiante_lanzaCredencialInvalida() {
        Usuario admin = new Usuario();
        ReflectionTestUtils.setField(admin, "id", 2L);
        admin.setRol(Usuario.Rol.ADMIN);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));

        assertThrows(CredencialInvalidaException.class, () ->
                progresoEstudianteService.completarNivelSiCorresponde(2L, 1L));
    }

    @Test
    void completarNivelSiCorresponde_nivelInexistente_lanzaExcepcion() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(nivelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                progresoEstudianteService.completarNivelSiCorresponde(10L, 99L));
    }

    @Test
    void completarNivelSiCorresponde_sinEjercicios_retornaFalse() {
        nivel.setEjercicios(Collections.emptyList());
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel));

        boolean resultado = progresoEstudianteService.completarNivelSiCorresponde(10L, 1L);

        assertFalse(resultado);
    }

    @Test
    void completarNivelSiCorresponde_ejerciciosNull_retornaFalse() {
        nivel.setEjercicios(null);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel));

        boolean resultado = progresoEstudianteService.completarNivelSiCorresponde(10L, 1L);

        assertFalse(resultado);
    }

    @Test
    void completarNivelSiCorresponde_ejercicioNoCompletado_retornaFalse() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel));
        when(intentoRepository.existsByUsuarioIdAndEjercicioIdAndCorrectaTrue(10L, 100L)).thenReturn(false);

        boolean resultado = progresoEstudianteService.completarNivelSiCorresponde(10L, 1L);

        assertFalse(resultado);
    }

    @Test
    void completarNivelSiCorresponde_todosCompletadosSinProgresoPrevio_guardaProgresoYRetornaTrue() {
        ArgumentCaptor<ProgresoEstudiante> captor = ArgumentCaptor.forClass(ProgresoEstudiante.class);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel));
        when(intentoRepository.existsByUsuarioIdAndEjercicioIdAndCorrectaTrue(10L, 100L)).thenReturn(true);
        when(progresoRepository.findByUsuarioIdAndNivelId(10L, 1L)).thenReturn(Optional.empty());

        boolean resultado = progresoEstudianteService.completarNivelSiCorresponde(10L, 1L);

        assertTrue(resultado);
        verify(progresoRepository).save(captor.capture());
        ProgresoEstudiante guardado = captor.getValue();
        assertEquals(estudiante, guardado.getUsuario());
        assertEquals(nivel, guardado.getNivel());
        assertNotNull(guardado.getFechaCompletado());
    }

    @Test
    void completarNivelSiCorresponde_todosCompletadosConProgresoPrevio_noDuplicaYRetornaTrue() {
        ProgresoEstudiante existente = new ProgresoEstudiante();
        existente.setUsuario(estudiante);
        existente.setNivel(nivel);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(nivelRepository.findById(1L)).thenReturn(Optional.of(nivel));
        when(intentoRepository.existsByUsuarioIdAndEjercicioIdAndCorrectaTrue(10L, 100L)).thenReturn(true);
        when(progresoRepository.findByUsuarioIdAndNivelId(10L, 1L)).thenReturn(Optional.of(existente));

        boolean resultado = progresoEstudianteService.completarNivelSiCorresponde(10L, 1L);

        assertTrue(resultado);
        verify(progresoRepository, never()).save(any(ProgresoEstudiante.class));
    }

    @Test
    void listarProgreso_estudianteValido_retornaListaDTO() {
        ProgresoEstudiante progreso = new ProgresoEstudiante();
        progreso.setUsuario(estudiante);
        progreso.setNivel(nivel);
        progreso.setFechaCompletado(LocalDateTime.now());

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(progresoRepository.findByUsuarioId(10L)).thenReturn(List.of(progreso));

        List<ProgresoEstudianteDTO> resultado = progresoEstudianteService.listarProgreso(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getNivelId());
        assertEquals("Nivel 1", resultado.get(0).getTitulo());
    }

    @Test
    void listarProgreso_estudianteInexistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                progresoEstudianteService.listarProgreso(99L));
    }

    @Test
    void listarProgreso_rolNoEstudiante_lanzaExcepcion() {
        Usuario admin = new Usuario();
        ReflectionTestUtils.setField(admin, "id", 2L);
        admin.setRol(Usuario.Rol.ADMIN);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(admin));

        assertThrows(CredencialInvalidaException.class, () ->
                progresoEstudianteService.listarProgreso(2L));
    }
}
