package ar.edu.unsam.programacion3.pythoncito.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unsam.programacion3.pythoncito.mappers.EjercicioMapper;
import ar.edu.unsam.programacion3.pythoncito.models.*;
import ar.edu.unsam.programacion3.pythoncito.dtos.*;
import ar.edu.unsam.programacion3.pythoncito.exceptions.*;
import ar.edu.unsam.programacion3.pythoncito.repositories.*;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final NivelRepository nivelRepository;
    private final IntentoService intentoService;
    private final ProgresoEstudianteService progresoEstudianteService;
    private final NivelService nivelService;

    public EjercicioService(
            EjercicioRepository ejercicioRepository, NivelRepository nivelRepository,
            IntentoService intentoService, ProgresoEstudianteService progresoEstudianteService,
            NivelService nivelService) {
        this.ejercicioRepository = ejercicioRepository;
        this.nivelRepository = nivelRepository;
        this.intentoService = intentoService;
        this.progresoEstudianteService = progresoEstudianteService;
        this.nivelService = nivelService;
    }

    public List<EjercicioDTO> listarParaEstudiante() {
        List<Ejercicio> ejercicios = ejercicioRepository.findAll();
        List<EjercicioDTO> ejerciciosDTO = new ArrayList<>();
        for (Ejercicio ejercicio : ejercicios) {
            ejerciciosDTO.add(EjercicioMapper.aDTOEstudiante(ejercicio));
        }
        return ejerciciosDTO;
    }

    public List<EjercicioDTO> listarPorNivelParaEstudiante(
            Long nivelId, Long usuarioId) {

        nivelService.obtenerNivelParaEstudiante(nivelId, usuarioId);

        List<Ejercicio> ejercicios = buscarEjerciciosDelNivel(nivelId);
        ordenarPorPresentacion(ejercicios);

        return convertirADTOEstudiante(ejercicios);
    }

    private List<Ejercicio> buscarEjerciciosDelNivel(Long nivelId) {
        List<Ejercicio> ejerciciosDelNivel = new ArrayList<>();
        List<Ejercicio> todos = ejercicioRepository.findAll();
        for (Ejercicio ejercicio : todos) {
            if (perteneceAlNivel(ejercicio, nivelId)) {
                ejerciciosDelNivel.add(ejercicio);
            }
        }
        return ejerciciosDelNivel;
    }

    private boolean perteneceAlNivel(Ejercicio ejercicio, Long nivelId) {
        return ejercicio.getNivel() != null
                && ejercicio.getNivel().getId() != null
                && ejercicio.getNivel().getId().equals(nivelId);
    }

    private void ordenarPorPresentacion(List<Ejercicio> ejercicios) {
        for (int i = 0; i < ejercicios.size() - 1; i++) {
            for (int j = 0; j < ejercicios.size() - 1 - i; j++) {
                int ordenActual = obtenerOrdenPresentacion(ejercicios.get(j));
                int ordenSiguiente = obtenerOrdenPresentacion(ejercicios.get(j + 1));
                if (ordenActual > ordenSiguiente) {
                    Ejercicio temporal = ejercicios.get(j);
                    ejercicios.set(j, ejercicios.get(j + 1));
                    ejercicios.set(j + 1, temporal);
                }
            }
        }
    }

    private int obtenerOrdenPresentacion(Ejercicio ejercicio) {
        if (ejercicio.getOrdenPresentacion() == null) {
            return 0;
        }
        return ejercicio.getOrdenPresentacion();
    }

    private List<EjercicioDTO> convertirADTOEstudiante(List<Ejercicio> ejercicios) {
        List<EjercicioDTO> ejerciciosDTO = new ArrayList<>();
        for (Ejercicio ejercicio : ejercicios) {
            ejerciciosDTO.add(EjercicioMapper.aDTOEstudiante(ejercicio));
        }
        return ejerciciosDTO;
    }

    public EjercicioDTO obtenerEjercicioParaEstudiante(
            Long id, Long usuarioId) {

        Ejercicio ejercicio = buscarPorId(id);

        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", id);
        }

        nivelService.obtenerNivelParaEstudiante(
                ejercicio.getNivel().getId(),
                usuarioId);

        return EjercicioMapper.aDTOEstudiante(ejercicio);
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

    @Transactional(readOnly = true)
    public PistaDTO obtenerPista(
            Long ejercicioId, Integer orden, Long usuarioId) {

        if (orden == null || orden < 1) {
            throw new ValidacionException(
                    "Debe indicar un número de pista válido.");
        }

        Ejercicio ejercicio = buscarPorId(ejercicioId);

        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", ejercicioId);
        }

        nivelService.obtenerNivelParaEstudiante(
                ejercicio.getNivel().getId(),
                usuarioId);

        Pista pistaSolicitada = null;

        for (Pista pista : ejercicio.getPistas()) {
            if (orden.equals(pista.getOrden())) {
                pistaSolicitada = pista;
                break;
            }
        }

        if (pistaSolicitada == null) {
            throw new RecursoNoEncontradoException(
                    "Pista con orden " + orden + " en ejercicio " + ejercicioId);
        }

        long intentosFallidos = intentoService.contarIntentosFallidos(usuarioId, ejercicioId);

        if (intentosFallidos < orden) {
            throw new AccesoDenegadoException(
                    "Necesitás realizar " + orden
                            + " intento(s) incorrecto(s) para desbloquear esta pista.");
        }

        return new PistaDTO(
                pistaSolicitada.getId(),
                pistaSolicitada.getTexto(),
                pistaSolicitada.getOrden());
    }

    @Transactional
    public ResultadoValidacionDTO validarRespuesta(ValidacionPeticionDTO peticion, Long usuarioId) {

        validarPeticion(peticion);

        Ejercicio ejercicio = buscarPorId(peticion.getEjercicioId());

        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", peticion.getEjercicioId());
        }

        nivelService.obtenerNivelParaEstudiante(ejercicio.getNivel().getId(), usuarioId);

        List<BloqueCodigo> bloquesOficiales = obtenerBloques(ejercicio);
        List<Long> idsRecibidos = obtenerIdsRecibidos(peticion);

        List<Long> bloquesSobrantes = buscarSobrantes(bloquesOficiales, idsRecibidos);

        List<Long> bloquesFaltantes = buscarFaltantes(bloquesOficiales, idsRecibidos);

        List<BloqueCodigo> solucionOrdenada = ordenarPorSolucion(bloquesOficiales);

        List<Long> bloquesFueraDeOrden = buscarFueraDeOrden(
                solucionOrdenada, idsRecibidos, bloquesSobrantes, bloquesOficiales);

        boolean esCorrecta = bloquesFaltantes.isEmpty()
                && bloquesSobrantes.isEmpty()
                && bloquesFueraDeOrden.isEmpty();

        intentoService.registrarIntento(usuarioId, ejercicio.getId(), esCorrecta);

        if (esCorrecta) {
            progresoEstudianteService.completarNivelSiCorresponde(
                    usuarioId,
                    ejercicio.getNivel().getId());
        }

        return new ResultadoValidacionDTO(esCorrecta,
                construirMensaje(
                        esCorrecta,
                        bloquesFaltantes,
                        bloquesSobrantes,
                        bloquesFueraDeOrden),
                bloquesFaltantes,
                bloquesSobrantes,
                bloquesFueraDeOrden);
    }

    private void validarPeticion(ValidacionPeticionDTO peticion) {
        if (peticion == null || peticion.getEjercicioId() == null) {
            throw new ValidacionException("Debe indicar el identificador del ejercicio.");
        }
    }

    private List<BloqueCodigo> obtenerBloques(Ejercicio ejercicio) {
        if (ejercicio.getBloques() == null) {
            return new ArrayList<>();
        }
        return ejercicio.getBloques();
    }

    private List<Long> obtenerIdsRecibidos(ValidacionPeticionDTO peticion) {
        if (peticion.getBloqueIds() == null) {
            return new ArrayList<>();
        }
        return peticion.getBloqueIds();
    }

    private List<Long> buscarSobrantes(
            List<BloqueCodigo> bloquesOficiales,
            List<Long> idsRecibidos) {
        List<Long> sobrantes = new ArrayList<>();
        for (int i = 0; i < idsRecibidos.size(); i++) {
            Long idRecibido = idsRecibidos.get(i);
            if (!perteneceAlEjercicio(bloquesOficiales, idRecibido)
                    && !contieneId(sobrantes, idRecibido)) {
                sobrantes.add(idRecibido);
            }
        }
        return sobrantes;
    }

    private List<Long> buscarFaltantes(
            List<BloqueCodigo> bloquesOficiales,
            List<Long> idsRecibidos) {
        List<Long> faltantes = new ArrayList<>();
        for (int i = 0; i < bloquesOficiales.size(); i++) {
            Long idOficial = bloquesOficiales.get(i).getId();
            if (!contieneId(idsRecibidos, idOficial)) {
                faltantes.add(idOficial);
            }
        }
        return faltantes;
    }

    private List<BloqueCodigo> ordenarPorSolucion(List<BloqueCodigo> bloquesOficiales) {
        List<BloqueCodigo> ordenados = new ArrayList<>(bloquesOficiales);
        for (int i = 0; i < ordenados.size() - 1; i++) {
            for (int j = 0; j < ordenados.size() - 1 - i; j++) {
                Integer ordenActual = obtenerOrdenCorrecto(ordenados.get(j));
                Integer ordenSiguiente = obtenerOrdenCorrecto(ordenados.get(j + 1));
                if (ordenActual > ordenSiguiente) {
                    BloqueCodigo temporal = ordenados.get(j);
                    ordenados.set(j, ordenados.get(j + 1));
                    ordenados.set(j + 1, temporal);
                }
            }
        }
        return ordenados;
    }

    private Integer obtenerOrdenCorrecto(BloqueCodigo bloque) {
        if (bloque.getOrdenCorrecto() == null) {
            return 0;
        }
        return bloque.getOrdenCorrecto();
    }

    private List<Long> buscarFueraDeOrden(
            List<BloqueCodigo> solucionOrdenada,
            List<Long> idsRecibidos,
            List<Long> bloquesSobrantes,
            List<BloqueCodigo> bloquesOficiales) {
        List<Long> fueraDeOrden = new ArrayList<>();
        for (int i = 0; i < idsRecibidos.size(); i++) {
            Long idRecibido = idsRecibidos.get(i);
            if (!contieneId(bloquesSobrantes, idRecibido)
                    && estaFueraDePosicion(solucionOrdenada, idsRecibidos, i, bloquesOficiales)
                    && !contieneId(fueraDeOrden, idRecibido)) {
                fueraDeOrden.add(idRecibido);
            }
        }
        return fueraDeOrden;
    }

    private boolean estaFueraDePosicion(
            List<BloqueCodigo> solucionOrdenada,
            List<Long> idsRecibidos,
            int posicion,
            List<BloqueCodigo> bloquesOficiales) {
        if (posicion >= solucionOrdenada.size()) {
            return true;
        }
        Integer ordenEsperado = solucionOrdenada.get(posicion).getOrdenCorrecto();
        BloqueCodigo bloqueActual = buscarBloquePorId(bloquesOficiales, idsRecibidos.get(posicion));
        if (bloqueActual == null || bloqueActual.getOrdenCorrecto() == null) {
            return true;
        }
        return !bloqueActual.getOrdenCorrecto().equals(ordenEsperado);
    }

    private BloqueCodigo buscarBloquePorId(List<BloqueCodigo> bloques, Long id) {
        for (int i = 0; i < bloques.size(); i++) {
            if (bloques.get(i).getId() != null && bloques.get(i).getId().equals(id)) {
                return bloques.get(i);
            }
        }
        return null;
    }

    private boolean perteneceAlEjercicio(List<BloqueCodigo> bloques, Long id) {
        for (int i = 0; i < bloques.size(); i++) {
            if (bloques.get(i).getId() != null && bloques.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean contieneId(List<Long> ids, Long id) {
        for (int i = 0; i < ids.size(); i++) {
            if (id != null && id.equals(ids.get(i))) {
                return true;
            }
        }
        return false;
    }

    private String construirMensaje(
            boolean correcta,
            List<Long> faltantes,
            List<Long> sobrantes,
            List<Long> fueraDeOrden) {
        if (correcta) {
            return "Excelente, la solucion es correcta.";
        }
        if (!faltantes.isEmpty() && !sobrantes.isEmpty()) {
            return "Faltan bloques necesarios y sobran bloques que no corresponden.";
        }
        if (!faltantes.isEmpty()) {
            return "Faltan bloques para completar la solucion.";
        }
        if (!sobrantes.isEmpty()) {
            return "Hay bloques que no pertenecen a este ejercicio.";
        }
        if (!fueraDeOrden.isEmpty()) {
            return "El orden de los bloques no es correcto. Revisa la secuencia logica.";
        }
        return "La respuesta es incorrecta.";
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
