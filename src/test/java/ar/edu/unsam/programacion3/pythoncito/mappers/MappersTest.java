package ar.edu.unsam.programacion3.pythoncito.mappers;

import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.models.BloqueCodigo;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.models.Pista;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class MappersTest {

    private Ejercicio ejercicioDePrueba() {
        Nivel nivel = new Nivel();
        ReflectionTestUtils.setField(nivel, "id", 7L);

        Ejercicio ejercicio = new Ejercicio();
        ReflectionTestUtils.setField(ejercicio, "id", 3L);
        ejercicio.setTitulo("Titulo");
        ejercicio.setConsigna("Consigna");
        ejercicio.setExplicacion("Explicacion");
        ejercicio.setEjemploDiferente("Ejemplo");
        ejercicio.setResultadoEjemplo("Resultado");
        ejercicio.setCodigoEsperado("print(1)");
        ejercicio.setOrdenPresentacion(4);
        ejercicio.setNivel(nivel);

        BloqueCodigo bloque = new BloqueCodigo();
        ReflectionTestUtils.setField(bloque, "id", 10L);
        bloque.setContenido("print(1)");
        bloque.setOrdenPresentacion(2);
        bloque.setOrdenCorrecto(1);
        bloque.setEjercicio(ejercicio);
        ejercicio.getBloques().add(bloque);

        Pista pista = new Pista();
        ReflectionTestUtils.setField(pista, "id", 20L);
        pista.setTexto("Pista 1");
        pista.setOrden(1);
        pista.setEjercicio(ejercicio);
        ejercicio.getPistas().add(pista);

        return ejercicio;
    }

    @Test
    void ejercicioMapper_aDTOEstudiante_copiaCamposYBloquesSinOrdenCorrecto() {
        EjercicioDTO dto = EjercicioMapper.aDTOEstudiante(ejercicioDePrueba());

        assertEquals(3L, dto.getId());
        assertEquals("Titulo", dto.getTitulo());
        assertEquals("Consigna", dto.getConsigna());
        assertEquals("Explicacion", dto.getExplicacion());
        assertEquals("Ejemplo", dto.getEjemploDiferente());
        assertEquals("Resultado", dto.getResultadoEjemplo());
        assertEquals(1, dto.getBloques().size());
        assertEquals(10L, dto.getBloques().get(0).getId());
        assertEquals("print(1)", dto.getBloques().get(0).getContenido());
        assertEquals(2, dto.getBloques().get(0).getOrdenPresentacion());
    }

    @Test
    void ejercicioMapper_aDTOAdmin_copiaTodoIncluyendoSolucionYPistas() {
        EjercicioAdminDTO dto = EjercicioMapper.aDTOAdmin(ejercicioDePrueba());

        assertEquals(3L, dto.getId());
        assertEquals("print(1)", dto.getCodigoEsperado());
        assertEquals(4, dto.getOrdenPresentacion());
        assertEquals(7L, dto.getNivelId());
        assertEquals(1, dto.getBloques().size());
        assertEquals(1, dto.getBloques().get(0).getOrdenCorrecto());
        assertEquals(2, dto.getBloques().get(0).getOrdenPresentacion());
        assertEquals(1, dto.getPistas().size());
        assertEquals(20L, dto.getPistas().get(0).getId());
        assertEquals("Pista 1", dto.getPistas().get(0).getTexto());
        assertEquals(1, dto.getPistas().get(0).getOrden());
    }

    @Test
    void nivelMapper_aDTO_copiaCampos() {
        Nivel nivel = new Nivel();
        ReflectionTestUtils.setField(nivel, "id", 4L);
        nivel.setTitulo("Nivel 4");
        nivel.setTema("Funciones");
        nivel.setActivo(true);
        nivel.setOrdenPresentacion(2);

        NivelDTO dto = NivelMapper.aDTO(nivel);

        assertEquals(4L, dto.getId());
        assertEquals("Nivel 4", dto.getTitulo());
        assertEquals("Funciones", dto.getTema());
        assertTrue(dto.getActivo());
        assertEquals(2, dto.getOrdenPresentacion());
        assertFalse(dto.isCompletado());
        assertFalse(dto.isDesbloqueado());
    }
}