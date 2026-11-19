package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.ProgresoEstudianteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NivelServiceOrdenamientosTest {

    @Mock
    private NivelRepository nivelRepository;
    @Mock
    private ProgresoEstudianteRepository progresoRepository;

    @InjectMocks
    private NivelService nivelService;

    private Nivel nivel(String titulo, Boolean activo, Integer orden) {
        Nivel n = new Nivel();
        n.setTitulo(titulo);
        n.setActivo(activo);
        n.setOrdenPresentacion(orden);
        return n;
    }

    @Test
    void listarActivos_ignoraActivoNuloYTrataOrdenNuloComoCero() {
        when(nivelRepository.findAll()).thenReturn(List.of(
                nivel("A", true, 2),
                nivel("B", true, null),
                nivel("C", null, 1),
                nivel("D", false, 3)));

        List<String> titulos = nivelService.listarActivos().stream()
                .map(NivelDTO::getTitulo)
                .toList();

        assertEquals(List.of("B", "A"), titulos);
    }
}