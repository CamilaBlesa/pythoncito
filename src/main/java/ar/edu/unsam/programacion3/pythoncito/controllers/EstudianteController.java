package ar.edu.unsam.programacion3.pythoncito.controllers;

import ar.edu.unsam.programacion3.pythoncito.dtos.*;
import ar.edu.unsam.programacion3.pythoncito.services.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteController {

    private final NivelService nivelService;
    private final EjercicioService ejercicioService;
    private final IntentoService intentoService;
    private final ProgresoEstudianteService progresoEstudianteService;

    public EstudianteController(NivelService nivelService,
            EjercicioService ejercicioService,
            IntentoService intentoService,
            ProgresoEstudianteService progresoEstudianteService) {
        this.nivelService = nivelService;
        this.ejercicioService = ejercicioService;
        this.intentoService = intentoService;
        this.progresoEstudianteService = progresoEstudianteService;
    }

    @GetMapping("/niveles")
    public ResponseEntity<List<NivelDTO>> listarNivelesActivos() {
        return ResponseEntity.ok(nivelService.listarActivos());
    }

    @GetMapping("/niveles/{id}")
    public ResponseEntity<NivelDTO> obtenerNivel(@PathVariable Long id) {
        return ResponseEntity.ok(nivelService.obtenerPorId(id));
    }

    @GetMapping("/niveles/{id}/ejercicios")
    public ResponseEntity<List<EjercicioDTO>> listarEjerciciosPorNivel(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.listarPorNivelParaEstudiante(id));
    }

    @GetMapping("/ejercicios/{id}")
    public ResponseEntity<EjercicioDTO> obtenerEjercicio(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.obtenerEjercicioParaEstudiante(id));
    }

    @GetMapping("/ejercicios/{id}/intentos")
    public ResponseEntity<List<IntentoDTO>> listarIntentos(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                intentoService.listarPorEjercicio(usuarioId, id));
    }

    @GetMapping("/ejercicios/{id}/pistas/{orden}")
    public ResponseEntity<PistaDTO> obtenerPista(@PathVariable Long id, @PathVariable Integer orden) {
        return ResponseEntity.ok(ejercicioService.obtenerPista(id, orden));
    }

    @PostMapping("/ejercicios/{id}/validar")
    public ResponseEntity<ResultadoValidacionDTO> validarRespuesta(
            @PathVariable Long id,
            @RequestBody List<Long> bloqueIds,
            @AuthenticationPrincipal Jwt jwt) {

        Long usuarioId = Long.valueOf(jwt.getSubject());

        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(id, bloqueIds);

        return ResponseEntity.ok(
                ejercicioService.validarRespuesta(peticion, usuarioId));
    }

    @PostMapping("/ejercicios/validar")
    public ResponseEntity<ResultadoValidacionDTO> validarRespuestaDirecta(
            @RequestBody ValidacionPeticionDTO peticion,
            @AuthenticationPrincipal Jwt jwt) {

        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                ejercicioService.validarRespuesta(peticion, usuarioId));
    }

    @GetMapping("/progreso")
    public ResponseEntity<List<ProgresoEstudianteDTO>> listarProgreso(
            @AuthenticationPrincipal Jwt jwt) {

        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                progresoEstudianteService.listarProgreso(usuarioId));
    }
}
