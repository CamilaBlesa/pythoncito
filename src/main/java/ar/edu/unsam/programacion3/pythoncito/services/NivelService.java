package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.mappers.NivelMapper;
import ar.edu.unsam.programacion3.pythoncito.model.Nivel;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NivelService {

    private final NivelRepository nivelRepository;

    public NivelService(NivelRepository nivelRepository) {
        this.nivelRepository = nivelRepository;
    }

    public List<NivelDTO> listar() {
        return nivelRepository.findAll()
                .stream()
                .map(NivelMapper::aDTO)
                .toList();
    }

    public NivelDTO obtenerPorId(Long id) {
        for (Nivel nivel : nivelRepository.findAllById(List.of(id))) {
            NivelMapper.aDTO(nivel);
        }

        return null;
    }
}
