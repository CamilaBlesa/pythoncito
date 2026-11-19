package ar.edu.unsam.programacion3.pythoncito.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unsam.programacion3.pythoncito.dtos.IntentoDTO;
import ar.edu.unsam.programacion3.pythoncito.exceptions.RecursoNoEncontradoException;
import ar.edu.unsam.programacion3.pythoncito.exceptions.CredencialInvalidaException;
import ar.edu.unsam.programacion3.pythoncito.models.Ejercicio;
import ar.edu.unsam.programacion3.pythoncito.models.Intento;
import ar.edu.unsam.programacion3.pythoncito.models.Usuario;
import ar.edu.unsam.programacion3.pythoncito.repositories.EjercicioRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.IntentoRepository;
import ar.edu.unsam.programacion3.pythoncito.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado del registro, consulta y conteo de intentos de resolucion de ejercicios.
 */
@Service
public class IntentoService {

        private final IntentoRepository intentoRepository;
        private final EjercicioRepository ejercicioRepository;
        private final UsuarioRepository usuarioRepository;
        private final NivelService nivelService;

        public IntentoService(
                        IntentoRepository intentoRepository,
                        EjercicioRepository ejercicioRepository,
                        UsuarioRepository usuarioRepository,
                        NivelService nivelService) {

                this.intentoRepository = intentoRepository;
                this.ejercicioRepository = ejercicioRepository;
                this.usuarioRepository = usuarioRepository;
                this.nivelService = nivelService;
        }

        /**
         * Registra un nuevo intento de resolucion calculando correlativamente su numero de intento.
         * @param usuarioId identificador del estudiante
         * @param ejercicioId identificador del ejercicio
         * @param correcta booleano que indica si la resolucion fue exitosa
         * @return DTO con el intento registrado
         */
        @Transactional
        public IntentoDTO registrarIntento(
                        Long usuarioId,
                        Long ejercicioId,
                        Boolean correcta) {

                Usuario estudiante = buscarEstudiante(usuarioId);
                Ejercicio ejercicio = buscarEjercicio(ejercicioId);

                Intento ultimoIntento = intentoRepository.findTopByUsuarioIdAndEjercicioIdOrderByNumeroIntentoDesc(
                                usuarioId,
                                ejercicioId);

                int siguienteNumero = ultimoIntento == null
                                ? 1
                                : ultimoIntento.getNumeroIntento() + 1;

                Intento intento = new Intento();
                intento.setUsuario(estudiante);
                intento.setEjercicio(ejercicio);
                intento.setNumeroIntento(siguienteNumero);
                intento.setCorrecta(correcta);
                intento.setFechaHora(LocalDateTime.now(ZoneId.systemDefault()));

                return convertirADTO(intentoRepository.save(intento));
        }

        /**
         * Lista cronologicamente los intentos de un estudiante en un ejercicio especifico.
         * @param usuarioId identificador del estudiante
         * @param ejercicioId identificador del ejercicio
         * @return lista de intentos realizados
         */
        @Transactional(readOnly = true)
        public List<IntentoDTO> listarPorEjercicio(
                        Long usuarioId,
                        Long ejercicioId) {

                buscarEstudiante(usuarioId);

                Ejercicio ejercicio = buscarEjercicio(ejercicioId);

                nivelService.obtenerNivelParaEstudiante(
                                ejercicio.getNivel().getId(),
                                usuarioId);

                List<Intento> intentos = intentoRepository.findByUsuarioIdAndEjercicioIdOrderByNumeroIntentoAsc(
                                usuarioId,
                                ejercicioId);

                List<IntentoDTO> intentosDTO = new ArrayList<>();

                for (Intento intento : intentos) {
                        intentosDTO.add(convertirADTO(intento));
                }

                return intentosDTO;
        }

        private Usuario buscarEstudiante(Long usuarioId) {

                Usuario estudiante = usuarioRepository.findById(usuarioId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", usuarioId));

                if (estudiante.getRol() != Usuario.Rol.ESTUDIANTE) {
                        throw new CredencialInvalidaException(
                                        "El usuario autenticado no es un estudiante.");
                }

                return estudiante;
        }

        private Ejercicio buscarEjercicio(Long ejercicioId) {

                Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                                .orElse(null);

                if (ejercicio == null) {
                        throw new RecursoNoEncontradoException("Ejercicio", ejercicioId);
                }

                return ejercicio;
        }

        private IntentoDTO convertirADTO(Intento intento) {

                return new IntentoDTO(
                                intento.getId(),
                                intento.getEjercicio().getId(),
                                intento.getNumeroIntento(),
                                intento.getCorrecta(),
                                intento.getFechaHora());
        }

        /**
         * Cuenta la cantidad de intentos fallidos registrados por un alumno para un ejercicio.
         * @param usuarioId identificador del estudiante
         * @param ejercicioId identificador del ejercicio
         * @return cantidad de intentos incorrectos
         */
        @Transactional(readOnly = true)
        public long contarIntentosFallidos(Long usuarioId, Long ejercicioId) {

                buscarEstudiante(usuarioId);
                buscarEjercicio(ejercicioId);

                return intentoRepository.countByUsuarioIdAndEjercicioIdAndCorrectaFalse(
                                usuarioId,
                                ejercicioId);
        }
}
