package ar.edu.unsam.programacion3.pythoncito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.PistaDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.ResultadoValidacionDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.ValidacionPeticionDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.models.BloqueCodigo;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.repositories.EjercicioRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;

@ExtendWith(MockitoExtension.class)
class EjercicioServiceNulosTest {

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

    private Nivel nivel(Long id) {
        Nivel n = new Nivel();
        if (id != null) {
            ReflectionTestUtils.setField(n, "id", id);
        }
        return n;
    }

    private Ejercicio ejercicio(Long id, Nivel nivel) {
        Ejercicio e = new Ejercicio();
        ReflectionTestUtils.setField(e, "id", id);
        e.setNivel(nivel);
        return e;
    }

    private void bloque(Ejercicio e, Long id, Integer ordenCorrecto) {
        BloqueCodigo b = new BloqueCodigo();
        if (id != null) {
            ReflectionTestUtils.setField(b, "id", id);
        }
        b.setContenido("contenido");
        b.setOrdenCorrecto(ordenCorrecto);
        b.setOrdenPresentacion(1);
        b.setEjercicio(e);
        e.getBloques().add(b);
    }

    @Test
    void validarRespuesta_ejercicioSinColeccionDeBloques_todoEsSobrante() {
        Ejercicio e = ejercicio(1L, nivel(7L));
        e.setBloques(null);
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(e));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(5L)), 5L);

        assertFalse(r.getCorrecta());
        assertEquals(List.of(5L), r.getBloquesSobrantes());
        assertEquals("Hay bloques que no pertenecen a este ejercicio.", r.getMensaje());
    }

    @Test
    void validarRespuesta_bloqueSinOrdenCorrecto_nuncaEsCorrecta() {
        Ejercicio e = ejercicio(1L, nivel(7L));
        bloque(e, 1L, 1);
        bloque(e, 2L, 2);
        bloque(e, 3L, null);
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(e));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(1L, 2L, 3L)), 5L);

        assertFalse(r.getCorrecta());
        assertEquals("El orden de los bloques no es correcto. Revisa la secuencia logica.", r.getMensaje());
    }

    @Test
    void validarRespuesta_bloqueOficialSinId_noRompeYMarcaFaltanteYSobrante() {
        Ejercicio e = ejercicio(1L, nivel(7L));
        bloque(e, null, 1);
        bloque(e, 1L, 2);
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(e));

        ResultadoValidacionDTO r = ejercicioService.validarRespuesta(
                new ValidacionPeticionDTO(1L, List.of(1L, 2L)), 5L);

        assertFalse(r.getCorrecta());
        assertEquals("Faltan bloques necesarios y sobran bloques que no corresponden.", r.getMensaje());
        assertEquals(List.of(2L), r.getBloquesSobrantes());
        assertEquals(1, r.getBloquesFaltantes().size());
    }

    @Test
    void listarPorNivel_ignoraEjerciciosSinNivelYTrataOrdenNuloComoCero() {
        Nivel n7 = nivel(7L);

        Ejercicio sinNivel = ejercicio(1L, null);
        Ejercicio nivelSinId = ejercicio(2L, nivel(null));
        Ejercicio otroNivel = ejercicio(3L, nivel(8L));

        Ejercicio conOrden = ejercicio(4L, n7);
        conOrden.setTitulo("conOrden");
        conOrden.setOrdenPresentacion(2);

        Ejercicio sinOrden = ejercicio(5L, n7);
        sinOrden.setTitulo("sinOrden");

        when(ejercicioRepository.findAll())
                .thenReturn(List.of(sinNivel, nivelSinId, otroNivel, conOrden, sinOrden));

        List<EjercicioDTO> resultado = ejercicioService.listarPorNivelParaEstudiante(7L, 5L);

        assertEquals(List.of("sinOrden", "conOrden"),
                resultado.stream()
                        .map(EjercicioDTO::getTitulo)
                        .toList());
    }

    @Test
    void crear_sinNivelId_lanzaRecursoNoEncontrado() {
        EjercicioAdminDTO datos = new EjercicioAdminDTO();

        assertThrows(RecursoNoEncontradoException.class, () -> ejercicioService.crear(datos));
    }

    @Test
    void actualizar_ejercicioSinColecciones_creaListasNuevas() {
        Nivel n7 = nivel(7L);
        Ejercicio e = ejercicio(1L, n7);
        e.setBloques(null);
        e.setPistas(null);

        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(e));
        when(nivelRepository.findById(7L)).thenReturn(Optional.of(n7));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenAnswer(inv -> inv.getArgument(0));

        EjercicioAdminDTO datos = new EjercicioAdminDTO();
        datos.setNivelId(7L);
        datos.setBloques(new ArrayList<>(List.of(new BloqueCodigoAdminDTO(null, "b", 1, 1))));
        datos.setPistas(new ArrayList<>(List.of(new PistaDTO(null, "p", 1))));

        EjercicioAdminDTO resultado = ejercicioService.actualizar(1L, datos);

        assertEquals(1, resultado.getBloques().size());
        assertEquals(1, resultado.getPistas().size());
    }
}