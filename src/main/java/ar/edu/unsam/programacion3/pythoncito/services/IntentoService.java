
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

@Service
public class IntentoService {

    private final IntentoRepository intentoRepository;
    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRepository usuarioRepository;

    public IntentoService(
            IntentoRepository intentoRepository,
            EjercicioRepository ejercicioRepository,
            UsuarioRepository usuarioRepository) {

        this.intentoRepository = intentoRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public IntentoDTO registrarIntento(
            Long usuarioId,
            Long ejercicioId,
            Boolean correcta) {

        Usuario alumno = buscarAlumno(usuarioId);
        Ejercicio ejercicio = buscarEjercicio(ejercicioId);

        Intento ultimoIntento =
                intentoRepository.findTopByUsuarioIdAndEjercicioIdOrderByNumeroIntentoDesc(
                        usuarioId,
                        ejercicioId
                );

        int siguienteNumero = ultimoIntento == null
                ? 1
                : ultimoIntento.getNumeroIntento() + 1;

        Intento intento = new Intento();
        intento.setUsuario(alumno);
        intento.setEjercicio(ejercicio);
        intento.setNumeroIntento(siguienteNumero);
        intento.setCorrecta(correcta);
        intento.setFechaHora(LocalDateTime.now(ZoneId.systemDefault()));

        return convertirADTO(intentoRepository.save(intento));
    }

    @Transactional(readOnly = true)
    public List<IntentoDTO> listarPorEjercicio(
            Long usuarioId,
            Long ejercicioId) {

        buscarAlumno(usuarioId);
        buscarEjercicio(ejercicioId);

        List<Intento> intentos =
                intentoRepository.findByUsuarioIdAndEjercicioIdOrderByNumeroIntentoAsc(
                        usuarioId,
                        ejercicioId
                );

        List<IntentoDTO> intentosDTO = new ArrayList<>();

        for (Intento intento : intentos) {
            intentosDTO.add(convertirADTO(intento));
        }

        return intentosDTO;
    }

    private Usuario buscarAlumno(Long usuarioId) {

        Usuario alumno = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Alumno", usuarioId)
                );

        if (alumno.getRol() != Usuario.Rol.ALUMNO) {
            throw new CredencialInvalidaException(
                    "El usuario autenticado no es un alumno."
            );
        }

        return alumno;
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
                intento.getFechaHora()
        );
    }
}