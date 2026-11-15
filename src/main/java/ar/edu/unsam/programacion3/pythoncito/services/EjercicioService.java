package ar.edu.unsam.programacion3.pythoncito.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unsam.programacion3.pythoncito.mappers.EjercicioMapper;
import ar.edu.unsam.programacion3.pythoncito.dtos.*;
import ar.edu.unsam.programacion3.pythoncito.model.*;
import ar.edu.unsam.programacion3.pythoncito.repositories.*;
import ar.edu.unsam.programacion3.pythoncito.exception.*;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final NivelRepository nivelRepository;

    public EjercicioService(
            EjercicioRepository ejercicioRepository,
            NivelRepository nivelRepository) {
        this.ejercicioRepository = ejercicioRepository;
        this.nivelRepository = nivelRepository;
    }

    public List<EjercicioDTO> listarParaAlumno() {
        List<Ejercicio> ejercicios = ejercicioRepository.findAll();
        List<EjercicioDTO> ejerciciosDTO = new ArrayList<>();
        for (Ejercicio ejercicio : ejercicios) {
            ejerciciosDTO.add(EjercicioMapper.aDTOAlumno(ejercicio));
        }
        return ejerciciosDTO;
    }

    public EjercicioDTO obtenerEjercicioParaAlumno(Long id) {
        Ejercicio ejercicio = buscarPorId(id);
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", id);
        }
        return EjercicioMapper.aDTOAlumno(ejercicio);
    }

    public List<EjercicioAdminDTO> listarParaAdmin() {
        List<Ejercicio> ejercicios = ejercicioRepository.findAll();
        List<EjercicioAdminDTO> ejerciciosDTO = new ArrayList<>();
        for (Ejercicio ejercicio : ejercicios) {
            ejerciciosDTO.add(EjercicioMapper.aDTOAdmin(ejercicio));
        }
        return ejerciciosDTO;
    }

    public EjercicioAdminDTO obtenerEjercicioParaAdmin(Long id) {
        Ejercicio ejercicio = buscarPorId(id);
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", id);
        }
        return EjercicioMapper.aDTOAdmin(ejercicio);
    }

    public PistaDTO obtenerPista(Long ejercicioId, Integer orden) {
        Ejercicio ejercicio = buscarPorId(ejercicioId);
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", ejercicioId);
        }

        for (Pista pista : ejercicio.getPistas()) {
            if (orden.equals(pista.getOrden())) {
                return new PistaDTO(pista.getId(), pista.getTexto(), pista.getOrden());
            }
        }
        throw new RecursoNoEncontradoException("Pista con orden " + orden + " en ejercicio " + ejercicioId);
    }

    @Transactional
    public EjercicioAdminDTO crear(EjercicioAdminDTO datos) {
        Nivel nivel = buscarNivel(datos.getNivelId());
        if (nivel == null) {
            throw new RecursoNoEncontradoException("Nivel", datos.getNivelId());
        }

        Ejercicio ejercicio = new Ejercicio();
        actualizarDatos(ejercicio, datos, nivel);
        return EjercicioMapper.aDTOAdmin(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public EjercicioAdminDTO actualizar(Long id, EjercicioAdminDTO datos) {
        Ejercicio ejercicio = buscarPorId(id);
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", id);
        }
        Nivel nivel = buscarNivel(datos.getNivelId());
        if (nivel == null) {
            throw new RecursoNoEncontradoException("Nivel", datos.getNivelId());
        }

        actualizarDatos(ejercicio, datos, nivel);
        return EjercicioMapper.aDTOAdmin(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public void eliminar(Long id) {
        Ejercicio ejercicio = buscarPorId(id);
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", id);
        }

        ejercicioRepository.delete(ejercicio);
    }

    private Ejercicio buscarPorId(Long id) {
        return ejercicioRepository.findById(id).isPresent() ? ejercicioRepository.findById(id).get() : null;
    }

    private Nivel buscarNivel(Long id) {
        if (id == null) {
            return null;
        }
        return nivelRepository.findById(id).isPresent() ? nivelRepository.findById(id).get() : null;
    }

    private void actualizarDatos(Ejercicio ejercicio, EjercicioAdminDTO datos, Nivel nivel) {

        ejercicio.setTitulo(datos.getTitulo());
        ejercicio.setConsigna(datos.getConsigna());
        ejercicio.setExplicacion(datos.getExplicacion());
        ejercicio.setEjemploDiferente(datos.getEjemploDiferente());
        ejercicio.setResultadoEjemplo(datos.getResultadoEjemplo());
        ejercicio.setCodigoEsperado(datos.getCodigoEsperado());
        ejercicio.setOrdenPresentacion(datos.getOrdenPresentacion());
        ejercicio.setNivel(nivel);

        if (datos.getBloques() != null) {
            List<BloqueCodigo> bloques = new ArrayList<>();
            for (BloqueCodigoAdminDTO bloqueDTO : datos.getBloques()) {
                BloqueCodigo bloque = new BloqueCodigo();
                bloque.setContenido(bloqueDTO.getContenido());
                bloque.setOrdenPresentacion(bloqueDTO.getOrdenPresentacion());
                bloque.setOrdenCorrecto(bloqueDTO.getOrdenCorrecto());
                bloque.setEjercicio(ejercicio);
                bloques.add(bloque);
            }
            ejercicio.setBloques(bloques);
        }

        if (datos.getPistas() != null) {
            ArrayList<Pista> pistas = new ArrayList<>();
            for (PistaDTO pistaDTO : datos.getPistas()) {
                Pista pista = new Pista();
                pista.setTexto(pistaDTO.getTexto());
                pista.setOrden(pistaDTO.getOrden());
                pista.setEjercicio(ejercicio);
                pistas.add(pista);
            }
            ejercicio.setPistas(pistas);
        }
    }
}
