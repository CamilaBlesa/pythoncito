package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.mappers.NivelMapper;
import ar.edu.unsam.programacion3.pythoncito.models.Nivel;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NivelService {

    private final NivelRepository nivelRepository;

    public NivelService(NivelRepository nivelRepository) {
        this.nivelRepository = nivelRepository;
    }

    public List<NivelDTO> listar() {
        List<Nivel> niveles = nivelRepository.findAll();
        List<NivelDTO> nivelesDTO = new ArrayList<>();
        for (Nivel nivel : niveles) {
            nivelesDTO.add(NivelMapper.aDTO(nivel));
        }
        return nivelesDTO;
    }

    public List<NivelDTO> listarActivos() {
        List<Nivel> todos = nivelRepository.findAll();
        List<Nivel> activos = new ArrayList<>();
        for (Nivel nivel : todos) {
            if (nivel.getActivo() != null && nivel.getActivo()) {
                activos.add(nivel);
            }
        }
        for (int i = 0; i < activos.size() - 1; i++) {
            for (int j = 0; j < activos.size() - 1 - i; j++) {
                int orden1 = activos.get(j).getOrdenPresentacion() != null ? activos.get(j).getOrdenPresentacion() : 0;
                int orden2 = activos.get(j + 1).getOrdenPresentacion() != null ? activos.get(j + 1).getOrdenPresentacion() : 0;
                if (orden1 > orden2) {
                    Nivel temporal = activos.get(j);
                    activos.set(j, activos.get(j + 1));
                    activos.set(j + 1, temporal);
                }
            }
        }
        List<NivelDTO> nivelesDTO = new ArrayList<>();
        for (Nivel nivel : activos) {
            nivelesDTO.add(NivelMapper.aDTO(nivel));
        }
        return nivelesDTO;
    }

    public NivelDTO obtenerPorId(Long id) {
        Nivel nivel = nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", id));
        return NivelMapper.aDTO(nivel);
    }

    @Transactional
    public NivelDTO crear(NivelDTO dto) {
        Nivel nivel = new Nivel();
        nivel.setTitulo(dto.getTitulo());
        nivel.setTema(dto.getTema());
        nivel.setOrdenPresentacion(dto.getOrdenPresentacion());
        nivel.setActivo(dto.getActivo() == null || dto.getActivo());
        return NivelMapper.aDTO(nivelRepository.save(nivel));
    }

    @Transactional
    public NivelDTO modificar(Long id, NivelDTO dto) {
        Nivel nivel = nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", id));
        nivel.setTitulo(dto.getTitulo());
        nivel.setTema(dto.getTema());
        nivel.setOrdenPresentacion(dto.getOrdenPresentacion());
        if (dto.getActivo() != null) {
            nivel.setActivo(dto.getActivo());
        }
        return NivelMapper.aDTO(nivelRepository.save(nivel));
    }

    @Transactional
    public NivelDTO darDeBaja(Long id) {
        Nivel nivel = nivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", id));
        nivel.setActivo(false);
        return NivelMapper.aDTO(nivelRepository.save(nivel));
    }
}
