package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.PistaDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.ResultadoValidacionDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.ValidacionPeticionDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.AccesoDenegadoException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjercicioServiceReglasTest {

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

    private Nivel nivel;
    private Ejercicio ejercicio;

    @BeforeEach
    void setUp() {
        nivel = new Nivel();
        ReflectionTestUtils.setField(nivel, "id", 7L);

        ejercicio = new Ejercicio();
        ReflectionTestUtils.setField(ejercicio, "id", 1L);
        ejercicio.setNivel(nivel);

        for (int i = 1; i <= 3; i++) {
            BloqueCodigo bloque = new BloqueCodigo();
            ReflectionTestUtils.setField(bloque, "id", (long) i);
            bloque.setContenido("bloque " + i);
            bloque.setOrdenCorrecto(i);
            bloque.setOrdenPresentacion(i);
            bloque.setEjercicio(ejercicio);
            ejercicio.getBloques().add(bloque);

            Pista pista = new Pista();
            ReflectionTestUtils.setField(pista, "id", (long) i);
            pista.setTexto("pista " + i);
            pista.setOrden(i);
            pista.setEjercicio(ejercicio);
            ejercicio.getPistas().add(pista);
        }
    }

    @Test
    void obtenerPista_tres_conUnIntentoFallido_noEstaDisponible() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(intentoService.contarIntentosFallidos(5L, 1L)).thenReturn(1L);

        assertThrows(AccesoDenegadoException.class,
                () -> ejercicioService.obtenerPista(1L, 3, 5L));
    }

    @Test
    void obtenerPista_tres_conDosIntentosFallidos_estaDisponible() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(intentoService.contarIntentosFallidos(5L, 1L)).thenReturn(2L);

        PistaDTO pista = ejercicioService.obtenerPista(1L, 3, 5L);

        assertEquals(3, pista.getOrden());
        assertEquals("pista 3", pista.getTexto());
    }

    @Test
    void validarRespuesta_correcta_completaNivelConLosIdsCorrectos() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(1L, 2L, 3L)), 5L);

        assertTrue(r.getCorrecta());
        assertEquals("Excelente, la solucion es correcta.", r.getMensaje());
        verify(intentoService).registrarIntento(5L, 1L, true);
        verify(progresoEstudianteService).completarNivelSiCorresponde(5L, 7L);
    }

    @Test
    void validarRespuesta_incorrecta_noCompletaNivelYRegistraIntentoFallido() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(3L, 2L, 1L)), 5L);

        assertFalse(r.getCorrecta());
        verify(intentoService).registrarIntento(5L, 1L, false);
        verify(progresoEstudianteService, never()).completarNivelSiCorresponde(any(), any());
    }

    @Test
    void validarRespuesta_faltanYSobran_devuelveMensajeCombinado() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(1L, 99L)), 5L);

        assertFalse(r.getCorrecta());
        assertEquals("Faltan bloques necesarios y sobran bloques que no corresponden.", r.getMensaje());
        assertEquals(List.of(2L, 3L), r.getBloquesFaltantes());
        assertEquals(List.of(99L), r.getBloquesSobrantes());
        assertTrue(r.getBloquesFueraDeOrden().isEmpty());
    }

    @Test
    void validarRespuesta_bloqueRepetido_seMarcaFueraDeOrden() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(1L, 1L, 2L, 3L)), 5L);

        assertFalse(r.getCorrecta());
        assertEquals("El orden de los bloques no es correcto. Revisa la secuencia logica.", r.getMensaje());
        assertTrue(r.getBloquesFaltantes().isEmpty());
        assertTrue(r.getBloquesSobrantes().isEmpty());
        assertEquals(List.of(1L, 2L, 3L), r.getBloquesFueraDeOrden());
    }

    @Test
    void crear_sinBloquesNiPistas_guardaConColeccionesVacias() {
        when(nivelRepository.findById(7L)).thenReturn(Optional.of(nivel));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenAnswer(inv -> inv.getArgument(0));

        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setTitulo("Nuevo");
        datos.setNivelId(7L);
        datos.setOrdenPresentacion(1);

        EjercicioAdminDTO resultado = ejercicioService.crear(datos);

        assertEquals("Nuevo", resultado.getTitulo());
        assertEquals(7L, resultado.getNivelId());
        assertTrue(resultado.getBloques().isEmpty());
        assertTrue(resultado.getPistas().isEmpty());
    }

    @Test
    void actualizar_reemplazaBloquesYConservaPistasSiVienenNulas() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicio));
        when(nivelRepository.findById(7L)).thenReturn(Optional.of(nivel));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenAnswer(inv -> inv.getArgument(0));

        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setNivelId(7L);
        datos.setBloques(new ArrayList<>(List.of(new BloqueCodigoAdminDTO(null, "unico", 1, 1))));

        EjercicioAdminDTO resultado = ejercicioService.actualizar(1L, datos);

        assertEquals(1, resultado.getBloques().size());
        assertEquals("unico", resultado.getBloques().get(0).getContenido());
        assertEquals(3, resultado.getPistas().size());
    }
}
