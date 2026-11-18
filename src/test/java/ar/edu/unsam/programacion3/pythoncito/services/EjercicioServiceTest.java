package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.PistaDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.ResultadoValidacionDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.ValidacionPeticionDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.AccesoDenegadoException;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.exceptions.ValidacionException;
import ar.edu.unsam.programacion3.pythoncito.models.BloqueCodigo;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.models.Pista;
import ar.edu.unsam.programacion3.pythoncito.repositories.EjercicioRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EjercicioServiceTest {

    @Mock
    private EjercicioRepository ejercicioRepository;

    @Mock
    private NivelRepository nivelRepository;

    @Mock
    private IntentoService intentoService;

    @Mock
    private ProgresoEstudianteService progresoEstudianteService;

    @Mock
    private NivelService nivelService;

    @InjectMocks
    private EjercicioService ejercicioService;

    private Ejercicio ejercicio;
    private Nivel nivel;
    private BloqueCodigo bloque1;
    private BloqueCodigo bloque2;
    private BloqueCodigo bloque3;

    @BeforeEach
    void setUp() {
        nivel = new Nivel();
        ReflectionTestUtils.setField(nivel, "id", 2L);
        nivel.setTitulo("Nivel 1");
        nivel.setTema("Variables");
        nivel.setActivo(true);
        nivel.setOrdenPresentacion(1);

        bloque1 = new BloqueCodigo();
        ReflectionTestUtils.setField(bloque1, "id", 1L);
        bloque1.setContenido("nombre = 'Guido'");
        bloque1.setOrdenPresentacion(2);
        bloque1.setOrdenCorrecto(1);

        bloque2 = new BloqueCodigo();
        ReflectionTestUtils.setField(bloque2, "id", 2L);
        bloque2.setContenido("print(nombre)");
        bloque2.setOrdenPresentacion(1);
        bloque2.setOrdenCorrecto(2);

        bloque3 = new BloqueCodigo();
        ReflectionTestUtils.setField(bloque3, "id", 3L);
        bloque3.setContenido("print('Fin')");
        bloque3.setOrdenPresentacion(3);
        bloque3.setOrdenCorrecto(3);

        ejercicio = new Ejercicio();
        ReflectionTestUtils.setField(ejercicio, "id", 1L);
        ejercicio.setTitulo("Reto Variables");
        ejercicio.setConsigna("Crea una variable y muestrala");
        ejercicio.setExplicacion("Explicacion");
        ejercicio.setEjemploDiferente("Ejemplo");
        ejercicio.setResultadoEjemplo("Resultado");
        ejercicio.setCodigoEsperado("Codigo");
        ejercicio.setOrdenPresentacion(1);
        ejercicio.setNivel(nivel);

        List<BloqueCodigo> bloques = new ArrayList<>(List.of(bloque1, bloque2, bloque3));
        ejercicio.setBloques(bloques);

        Pista pista1 = new Pista();
        ReflectionTestUtils.setField(pista1, "id", 10L);
        pista1.setTexto("Primero asigna el valor a la variable");
        pista1.setOrden(1);
        pista1.setEjercicio(ejercicio);

        Pista pista2 = new Pista();
        ReflectionTestUtils.setField(pista2, "id", 11L);
        pista2.setTexto("Luego usa print para mostrarla");
        pista2.setOrden(2);
        pista2.setEjercicio(ejercicio);

        ejercicio.setPistas(new ArrayList<>(List.of(pista1, pista2)));
    }

    @Test
    void validarRespuesta_ejercicioInexistente_lanzaExcepcion() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(999L, List.of(1L, 2L, 3L));
        when(ejercicioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.validarRespuesta(peticion, 5L));
    }

    @Test
    void validarRespuesta_peticionNula_lanzaExcepcion() {
        assertThrows(ValidacionException.class, () ->
                ejercicioService.validarRespuesta(null, 5L));
    }

    @Test
    void validarRespuesta_peticionSinEjercicioId_lanzaExcepcion() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(null, List.of(1L));
        assertThrows(ValidacionException.class, () ->
                ejercicioService.validarRespuesta(peticion, 5L));
    }

    @Test
    void validarRespuesta_peticionSinBloques_detectaFaltantes() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(1L, null);
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO resultado = ejercicioService.validarRespuesta(peticion, 5L);

        assertFalse(resultado.getCorrecta());
        assertEquals(3, resultado.getBloquesFaltantes().size());
        verify(intentoService).registrarIntento(5L, 1L, false);
    }

    @Test
    void validarRespuesta_secuenciaCorrecta_retornaExitoYRegistraIntento() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(1L, List.of(1L, 2L, 3L));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO resultado = ejercicioService.validarRespuesta(peticion, 5L);

        assertTrue(resultado.getCorrecta());
        assertEquals("Excelente, la solucion es correcta.", resultado.getMensaje());
        assertTrue(resultado.getBloquesFaltantes().isEmpty());
        assertTrue(resultado.getBloquesSobrantes().isEmpty());
        assertTrue(resultado.getBloquesFueraDeOrden().isEmpty());

        verify(intentoService).registrarIntento(5L, 1L, true);
        verify(progresoEstudianteService).completarNivelSiCorresponde(5L, 2L);
    }

    @Test
    void validarRespuesta_bloquesFaltantes_retornaFalso() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(1L, List.of(1L, 2L));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO resultado = ejercicioService.validarRespuesta(peticion, 5L);

        assertFalse(resultado.getCorrecta());
        assertEquals(List.of(3L), resultado.getBloquesFaltantes());
        assertTrue(resultado.getBloquesSobrantes().isEmpty());
        verify(intentoService).registrarIntento(5L, 1L, false);
        verify(progresoEstudianteService, never()).completarNivelSiCorresponde(anyLong(), anyLong());
    }

    @Test
    void validarRespuesta_bloquesSobrantes_retornaFalso() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(1L, List.of(1L, 2L, 3L, 999L));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO resultado = ejercicioService.validarRespuesta(peticion, 5L);

        assertFalse(resultado.getCorrecta());
        assertEquals(List.of(999L), resultado.getBloquesSobrantes());
        verify(intentoService).registrarIntento(5L, 1L, false);
    }

    @Test
    void validarRespuesta_bloquesDesordenados_retornaFalsoYDetectaPosiciones() {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(1L, List.of(2L, 1L, 3L));
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO resultado = ejercicioService.validarRespuesta(peticion, 5L);

        assertFalse(resultado.getCorrecta());
        assertFalse(resultado.getBloquesFueraDeOrden().isEmpty());
        verify(intentoService).registrarIntento(5L, 1L, false);
    }

    @Test
    void obtenerPista_ordenNulo_lanzaExcepcion() {
        assertThrows(ValidacionException.class, () ->
                ejercicioService.obtenerPista(1L, null, 5L));
    }

    @Test
    void obtenerPista_ordenMenorAUno_lanzaExcepcion() {
        assertThrows(ValidacionException.class, () ->
                ejercicioService.obtenerPista(1L, 0, 5L));
    }

    @Test
    void obtenerPista_ejercicioInexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.obtenerPista(999L, 1, 5L));
    }

    @Test
    void obtenerPista_pistaUno_retornaPistaDirectamente() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        PistaDTO pista = ejercicioService.obtenerPista(1L, 1, 5L);

        assertNotNull(pista);
        assertEquals(1, pista.getOrden());
        assertEquals("Primero asigna el valor a la variable", pista.getTexto());
    }

    @Test
    void obtenerPista_pistaDosConIntentosInsuficientes_lanzaAccesoDenegadoException() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(intentoService.contarIntentosFallidos(5L, 1L)).thenReturn(0L);

        assertThrows(AccesoDenegadoException.class, () ->
                ejercicioService.obtenerPista(1L, 2, 5L));
    }

    @Test
    void obtenerPista_pistaDosConIntentosSuficientes_retornaPista() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(intentoService.contarIntentosFallidos(5L, 1L)).thenReturn(1L);

        PistaDTO pista = ejercicioService.obtenerPista(1L, 2, 5L);

        assertNotNull(pista);
        assertEquals(2, pista.getOrden());
        assertEquals("Luego usa print para mostrarla", pista.getTexto());
    }

    @Test
    void obtenerPista_pistaInexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.obtenerPista(1L, 99, 5L));
    }

    @Test
    void listarParaEstudiante_retornaListaConvertida() {
        when(ejercicioRepository.findAll()).thenReturn(List.of(ejercicio));

        List<EjercicioDTO> resultado = ejercicioService.listarParaEstudiante();

        assertEquals(1, resultado.size());
        assertEquals("Reto Variables", resultado.get(0).getTitulo());
    }

    @Test
    void listarPorNivelParaEstudiante_ordenaPorBubbleSort() {
        Ejercicio ej1 = new Ejercicio();
        ReflectionTestUtils.setField(ej1, "id", 10L);
        ej1.setTitulo("Ej 1");
        ej1.setOrdenPresentacion(2);
        ej1.setNivel(nivel);

        Ejercicio ej2 = new Ejercicio();
        ReflectionTestUtils.setField(ej2, "id", 20L);
        ej2.setTitulo("Ej 2");
        ej2.setOrdenPresentacion(1);
        ej2.setNivel(nivel);

        when(ejercicioRepository.findAll()).thenReturn(List.of(ej1, ej2));

        List<EjercicioDTO> resultado = ejercicioService.listarPorNivelParaEstudiante(2L, 5L);

        assertEquals(2, resultado.size());
        assertEquals("Ej 2", resultado.get(0).getTitulo());
        assertEquals("Ej 1", resultado.get(1).getTitulo());
    }

    @Test
    void obtenerEjercicioParaEstudiante_existente_retornaDTO() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        EjercicioDTO resultado = ejercicioService.obtenerEjercicioParaEstudiante(1L, 5L);

        assertNotNull(resultado);
        assertEquals("Reto Variables", resultado.getTitulo());
    }

    @Test
    void obtenerEjercicioParaEstudiante_inexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.obtenerEjercicioParaEstudiante(99L, 5L));
    }

    @Test
    void listarParaAdmin_retornaTodos() {
        when(ejercicioRepository.findAll()).thenReturn(List.of(ejercicio));

        List<EjercicioAdminDTO> resultado = ejercicioService.listarParaAdmin();

        assertEquals(1, resultado.size());
        assertEquals("Reto Variables", resultado.get(0).getTitulo());
    }

    @Test
    void obtenerEjercicioParaAdmin_existente_retornaDTO() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        EjercicioAdminDTO resultado = ejercicioService.obtenerEjercicioParaAdmin(1L);

        assertNotNull(resultado);
        assertEquals("Reto Variables", resultado.getTitulo());
    }

    @Test
    void obtenerEjercicioParaAdmin_inexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.obtenerEjercicioParaAdmin(99L));
    }

    @Test
    void crear_nivelExistente_creaEjercicioYGuarda() {
        when(nivelRepository.findById(2L)).thenReturn(Optional.of(nivel));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenAnswer(inv -> {
            Ejercicio e = inv.getArgument(0);
            ReflectionTestUtils.setField(e, "id", 100L);
            return e;
        });

        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setTitulo("Nuevo Ejercicio");
        datos.setConsigna("Consigna");
        datos.setExplicacion("Explicacion");
        datos.setEjemploDiferente("Ejemplo");
        datos.setResultadoEjemplo("Resultado");
        datos.setCodigoEsperado("Codigo");
        datos.setOrdenPresentacion(1);
        datos.setNivelId(2L);

        EjercicioAdminDTO resultado = ejercicioService.crear(datos);

        assertNotNull(resultado);
        assertEquals("Nuevo Ejercicio", resultado.getTitulo());
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    void crear_nivelInexistente_lanzaExcepcion() {
        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setNivelId(999L);
        when(nivelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.crear(datos));
    }

    @Test
    void actualizar_ejercicioExistente_actualizaDatosYGuarda() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(nivelRepository.findById(2L)).thenReturn(Optional.of(nivel));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenAnswer(inv -> inv.getArgument(0));

        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setTitulo("Titulo Actualizado");
        datos.setConsigna("Consigna Actualizada");
        datos.setExplicacion("Explicacion");
        datos.setEjemploDiferente("Ejemplo");
        datos.setResultadoEjemplo("Resultado");
        datos.setCodigoEsperado("Codigo");
        datos.setOrdenPresentacion(2);
        datos.setNivelId(2L);

        EjercicioAdminDTO resultado = ejercicioService.actualizar(1L, datos);

        assertEquals("Titulo Actualizado", resultado.getTitulo());
        assertEquals("Consigna Actualizada", resultado.getConsigna());
        verify(ejercicioRepository).save(ejercicio);
    }

    @Test
    void actualizar_ejercicioInexistente_lanzaExcepcion() {
        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        when(ejercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.actualizar(99L, datos));
    }

    @Test
    void actualizar_nivelInexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setNivelId(999L);
        when(nivelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.actualizar(1L, datos));
    }

    @Test
    void eliminar_ejercicioExistente_eliminaDeRepositorio() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ejercicioService.eliminar(1L);

        verify(ejercicioRepository).delete(ejercicio);
    }

    @Test
    void eliminar_ejercicioInexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                ejercicioService.eliminar(99L));
    }
}
