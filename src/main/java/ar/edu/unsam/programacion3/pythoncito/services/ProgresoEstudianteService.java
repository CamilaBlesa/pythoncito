package ar.edu.unsam.programacion3.pythoncito.services;

import ar.edu.unsam.programacion3.pythoncito.models.*;
import ar.edu.unsam.programacion3.pythoncito.repositories.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unsam.programacion3.pythoncito.dtos.ProgresoEstudianteDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Servicio encargado del seguimiento de progreso de los estudiantes y desbloqueo de niveles.
 */
@Service
public class ProgresoEstudianteService {

        private final ProgresoEstudianteRepository progresoRepository;
        private final IntentoRepository intentoRepository;
        private final UsuarioRepository usuarioRepository;
        private final NivelRepository nivelRepository;

        public ProgresoEstudianteService(
                        ProgresoEstudianteRepository progresoRepository,
                        IntentoRepository intentoRepository,
                        UsuarioRepository usuarioRepository,
                        NivelRepository nivelRepository) {

                this.progresoRepository = progresoRepository;
                this.intentoRepository = intentoRepository;
                this.usuarioRepository = usuarioRepository;
                this.nivelRepository = nivelRepository;
        }

        /**
         * Verifica si todos los ejercicios de un nivel estan aprobados y, si corresponde, registra el progreso.
         * @param usuarioId identificador del estudiante
         * @param nivelId identificador del nivel evaluado
         * @return true si el nivel fue completado con exito, false en caso contrario
         */
        @Transactional
        public boolean completarNivelSiCorresponde(
                        Long usuarioId,
                        Long nivelId) {

                Usuario estudiante = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", usuarioId));

                if (estudiante.getRol() != Usuario.Rol.ESTUDIANTE) {
                        throw new CredencialInvalidaException(
                                        "El usuario no es un estudiante válido para completar niveles.");
                }

                Nivel nivel = nivelRepository.findById(nivelId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Nivel", nivelId));

                if (nivel.getEjercicios() == null || nivel.getEjercicios().isEmpty()) {
                        return false;
                }

                for (Ejercicio ejercicio : nivel.getEjercicios()) {

                        boolean ejercicioCompletado = intentoRepository.existsByUsuarioIdAndEjercicioIdAndCorrectaTrue(
                                        usuarioId,
                                        ejercicio.getId());

                        if (!ejercicioCompletado) {
                                return false;
                        }
                }

                ProgresoEstudiante progresoExistente = progresoRepository
                                .findByUsuarioIdAndNivelId(usuarioId, nivelId)
                                .orElse(null);

                if (progresoExistente == null) {

                        ProgresoEstudiante progreso = new ProgresoEstudiante();

                        progreso.setUsuario(estudiante);
                        progreso.setNivel(nivel);
                        progreso.setFechaCompletado(LocalDateTime.now(ZoneId.systemDefault()));

                        progresoRepository.save(progreso);
                }
                return true;
        }

        /**
         * Lista los niveles completados por un estudiante.
         * @param usuarioId identificador del estudiante
         * @return lista de DTOs con la informacion de niveles completados
         */
        @Transactional(readOnly = true)
        public List<ProgresoEstudianteDTO> listarProgreso(Long usuarioId) {

                Usuario estudiante = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", usuarioId));

                if (estudiante.getRol() != Usuario.Rol.ESTUDIANTE) {
                        throw new CredencialInvalidaException(
                                        "El usuario no es un estudiante");
                }

                List<ProgresoEstudiante> progresos = progresoRepository.findByUsuarioId(usuarioId);

                List<ProgresoEstudianteDTO> progresosDTO = new ArrayList<>();

                for (ProgresoEstudiante progreso : progresos) {

                        Nivel nivel = progreso.getNivel();

                        progresosDTO.add(new ProgresoEstudianteDTO(
                                        nivel.getId(),
                                        nivel.getTitulo(),
                                        nivel.getTema(),
                                        progreso.getFechaCompletado()));
                }

                return progresosDTO;
        }
}
