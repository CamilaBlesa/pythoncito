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

    public List<EjercicioDTO> listarPorNivelParaAlumno(Long nivelId) {
        Nivel nivel = buscarNivel(nivelId);
        if (nivel == null) {
            throw new RecursoNoEncontradoException("Nivel", nivelId);
        }
        List<Ejercicio> todos = ejercicioRepository.findAll();
        List<Ejercicio> delNivel = new ArrayList<>();
        for (Ejercicio ejercicio : todos) {
            if (ejercicio.getNivel() != null && ejercicio.getNivel().getId() != null && ejercicio.getNivel().getId().equals(nivelId)) {
                delNivel.add(ejercicio);
            }
        }
        for (int i = 0; i < delNivel.size() - 1; i++) {
            for (int j = 0; j < delNivel.size() - 1 - i; j++) {
                int orden1 = delNivel.get(j).getOrdenPresentacion() != null ? delNivel.get(j).getOrdenPresentacion() : 0;
                int orden2 = delNivel.get(j + 1).getOrdenPresentacion() != null ? delNivel.get(j + 1).getOrdenPresentacion() : 0;
                if (orden1 > orden2) {
                    Ejercicio temporal = delNivel.get(j);
                    delNivel.set(j, delNivel.get(j + 1));
                    delNivel.set(j + 1, temporal);
                }
            }
        }
        List<EjercicioDTO> ejerciciosDTO = new ArrayList<>();
        for (Ejercicio ejercicio : delNivel) {
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

    public ResultadoValidacionDTO validarRespuesta(ValidacionPeticionDTO peticion) {
        if (peticion == null || peticion.getEjercicioId() == null) {
            throw new ValidacionException("Debe indicar el identificador del ejercicio.");
        }
        Ejercicio ejercicio = buscarPorId(peticion.getEjercicioId());
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", peticion.getEjercicioId());
        }

        List<BloqueCodigo> bloquesOficiales = ejercicio.getBloques();
        if (bloquesOficiales == null) {
            bloquesOficiales = new ArrayList<>();
        }

        List<Long> idsRecibidos = peticion.getBloqueIds();
        if (idsRecibidos == null) {
            idsRecibidos = new ArrayList<>();
        }

        List<Long> bloquesSobrantes = new ArrayList<>();
        for (int i = 0; i < idsRecibidos.size(); i++) {
            Long idRecibido = idsRecibidos.get(i);
            boolean pertenece = false;
            for (int j = 0; j < bloquesOficiales.size(); j++) {
                if (bloquesOficiales.get(j).getId() != null && bloquesOficiales.get(j).getId().equals(idRecibido)) {
                    pertenece = true;
                    break;
                }
            }
            if (!pertenece) {
                boolean yaAgregado = false;
                for (int k = 0; k < bloquesSobrantes.size(); k++) {
                    if (bloquesSobrantes.get(k).equals(idRecibido)) {
                        yaAgregado = true;
                        break;
                    }
                }
                if (!yaAgregado) {
                    bloquesSobrantes.add(idRecibido);
                }
            }
        }

        List<Long> bloquesFaltantes = new ArrayList<>();
        for (int i = 0; i < bloquesOficiales.size(); i++) {
            Long idOficial = bloquesOficiales.get(i).getId();
            boolean estaPresente = false;
            for (int j = 0; j < idsRecibidos.size(); j++) {
                if (idOficial != null && idOficial.equals(idsRecibidos.get(j))) {
                    estaPresente = true;
                    break;
                }
            }
            if (!estaPresente) {
                bloquesFaltantes.add(idOficial);
            }
        }

        List<BloqueCodigo> solucionOrdenada = new ArrayList<>();
        for (int i = 0; i < bloquesOficiales.size(); i++) {
            solucionOrdenada.add(bloquesOficiales.get(i));
        }

        for (int i = 0; i < solucionOrdenada.size() - 1; i++) {
            for (int j = 0; j < solucionOrdenada.size() - 1 - i; j++) {
                int orden1 = solucionOrdenada.get(j).getOrdenCorrecto() != null ? solucionOrdenada.get(j).getOrdenCorrecto() : 0;
                int orden2 = solucionOrdenada.get(j + 1).getOrdenCorrecto() != null ? solucionOrdenada.get(j + 1).getOrdenCorrecto() : 0;
                if (orden1 > orden2) {
                    BloqueCodigo temporal = solucionOrdenada.get(j);
                    solucionOrdenada.set(j, solucionOrdenada.get(j + 1));
                    solucionOrdenada.set(j + 1, temporal);
                }
            }
        }

        List<Long> bloquesFueraDeOrden = new ArrayList<>();
        for (int i = 0; i < idsRecibidos.size(); i++) {
            Long idRecibido = idsRecibidos.get(i);
            boolean esSobrante = false;
            for (int s = 0; s < bloquesSobrantes.size(); s++) {
                if (bloquesSobrantes.get(s).equals(idRecibido)) {
                    esSobrante = true;
                    break;
                }
            }
            if (!esSobrante) {
                if (i < solucionOrdenada.size()) {
                    Long idEsperado = solucionOrdenada.get(i).getId();
                    if (idEsperado != null && !idEsperado.equals(idRecibido)) {
                        boolean yaListado = false;
                        for (int k = 0; k < bloquesFueraDeOrden.size(); k++) {
                            if (bloquesFueraDeOrden.get(k).equals(idRecibido)) {
                                yaListado = true;
                                break;
                            }
                        }
                        if (!yaListado) {
                            bloquesFueraDeOrden.add(idRecibido);
                        }
                    }
                } else {
                    boolean yaListado = false;
                    for (int k = 0; k < bloquesFueraDeOrden.size(); k++) {
                        if (bloquesFueraDeOrden.get(k).equals(idRecibido)) {
                            yaListado = true;
                            break;
                        }
                    }
                    if (!yaListado) {
                        bloquesFueraDeOrden.add(idRecibido);
                    }
                }
            }
        }

        boolean correcta = bloquesFaltantes.isEmpty() && bloquesSobrantes.isEmpty() && bloquesFueraDeOrden.isEmpty();

        String mensaje;
        if (correcta) {
            mensaje = "Excelente, la solucion es correcta.";
        } else if (!bloquesFaltantes.isEmpty() && !bloquesSobrantes.isEmpty()) {
            mensaje = "Faltan bloques necesarios y sobran bloques que no corresponden.";
        } else if (!bloquesFaltantes.isEmpty()) {
            mensaje = "Faltan bloques para completar la solucion.";
        } else if (!bloquesSobrantes.isEmpty()) {
            mensaje = "Hay bloques que no pertenecen a este ejercicio.";
        } else if (!bloquesFueraDeOrden.isEmpty()) {
            mensaje = "El orden de los bloques no es correcto. Revisa la secuencia logica.";
        } else {
            mensaje = "La respuesta es incorrecta.";
        }

        return new ResultadoValidacionDTO(correcta, mensaje, bloquesFaltantes, bloquesSobrantes, bloquesFueraDeOrden);
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
        return ejercicioRepository.findById(id).orElse(null);
    }

    private Nivel buscarNivel(Long id) {
        if (id == null) {
            return null;
        }
        return nivelRepository.findById(id).orElse(null);
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
