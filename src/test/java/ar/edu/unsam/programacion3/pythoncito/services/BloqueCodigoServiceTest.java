package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.models.BloqueCodigo;
import ar.edu.unsam.programacion3.pythoncito.repositories.BloqueCodigoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BloqueCodigoServiceTest {

    @Mock
    private BloqueCodigoRepository bloqueCodigoRepository;

    @InjectMocks
    private BloqueCodigoService bloqueCodigoService;

    private BloqueCodigo bloque;

    @BeforeEach
    void setUp() {
        bloque = new BloqueCodigo();
        ReflectionTestUtils.setField(bloque, "id", 1L);
        bloque.setContenido("x = 10");
        bloque.setOrdenPresentacion(2);
        bloque.setOrdenCorrecto(1);
    }

    @Test
    void obtenerPorId_existente_retornaDTO() {
        when(bloqueCodigoRepository.findById(1L)).thenReturn(Optional.of(bloque));

        BloqueCodigoAdminDTO dto = bloqueCodigoService.obtenerPorId(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("x = 10", dto.getContenido());
        assertEquals(2, dto.getOrdenPresentacion());
        assertEquals(1, dto.getOrdenCorrecto());
    }

    @Test
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(bloqueCodigoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                bloqueCodigoService.obtenerPorId(99L));
    }

    @Test
    void modificar_existente_actualizaYRetornaDTO() {
        when(bloqueCodigoRepository.findById(1L)).thenReturn(Optional.of(bloque));
        when(bloqueCodigoRepository.save(any(BloqueCodigo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BloqueCodigoAdminDTO actualizacion = new BloqueCodigoAdminDTO(1L, "print(x)", 1, 2);

        BloqueCodigoAdminDTO resultado = bloqueCodigoService.modificar(1L, actualizacion);

        assertNotNull(resultado);
        assertEquals("print(x)", resultado.getContenido());
        assertEquals(1, resultado.getOrdenPresentacion());
        assertEquals(2, resultado.getOrdenCorrecto());
        verify(bloqueCodigoRepository).save(bloque);
    }

    @Test
    void modificar_inexistente_lanzaExcepcion() {
        when(bloqueCodigoRepository.findById(99L)).thenReturn(Optional.empty());

        BloqueCodigoAdminDTO actualizacion = new BloqueCodigoAdminDTO(99L, "print(x)", 1, 2);

        assertThrows(RecursoNoEncontradoException.class, () ->
                bloqueCodigoService.modificar(99L, actualizacion));
    }
}
