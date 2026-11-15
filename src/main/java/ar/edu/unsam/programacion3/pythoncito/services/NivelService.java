package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.mappers.NivelMapper;
import ar.edu.unsam.programacion3.pythoncito.model.Nivel;
import ar.edu.unsam.programacion3.pythoncito.repositories.NivelRepository;
import org.springframework.stereotype.Service;

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

    public NivelDTO obtenerPorId(Long id) {
        Nivel nivel = nivelRepository.findById(id).orElse(null);
        if (nivel == null) {
            return null;
        }
        return NivelMapper.aDTO(nivel);
    }
}
