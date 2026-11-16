package ar.edu.unsam.programacion3.pythoncito.controllers;

import ar.edu.unsam.programacion3.pythoncito.dtos.*;
import ar.edu.unsam.programacion3.pythoncito.services.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumno")
public class AlumnoController {

    private final NivelService nivelService;
    private final EjercicioService ejercicioService;
    private final IntentoService intentoService;

    public AlumnoController(NivelService nivelService,
                            EjercicioService ejercicioService,
                            IntentoService intentoService) {
        this.nivelService = nivelService;
        this.ejercicioService = ejercicioService;
        this.intentoService = intentoService;
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
        return ResponseEntity.ok(ejercicioService.listarPorNivelParaAlumno(id));
    }

    @GetMapping("/ejercicios/{id}")
    public ResponseEntity<EjercicioDTO> obtenerEjercicio(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.obtenerEjercicioParaAlumno(id));
    }

    @GetMapping("/ejercicios/{id}/intentos")
    public ResponseEntity<List<IntentoDTO>> listarIntentos(@PathVariable Long id) {
        return ResponseEntity.ok(intentoService.listarPorEjercicio(id));
    }

    @GetMapping("/ejercicios/{id}/pistas/{orden}")
    public ResponseEntity<PistaDTO> obtenerPista(@PathVariable Long id, @PathVariable Integer orden) {
        return ResponseEntity.ok(ejercicioService.obtenerPista(id, orden));
    }

    @PostMapping("/ejercicios/{id}/validar")
    public ResponseEntity<ResultadoValidacionDTO> validarRespuesta(
            @PathVariable Long id,
            @RequestBody List<Long> bloqueIds) {
        ValidacionPeticionDTO peticion = new ValidacionPeticionDTO(id, bloqueIds);
        return ResponseEntity.ok(ejercicioService.validarRespuesta(peticion));
    }

    @PostMapping("/ejercicios/validar")
    public ResponseEntity<ResultadoValidacionDTO> validarRespuestaDirecta(
            @RequestBody ValidacionPeticionDTO peticion) {
        return ResponseEntity.ok(ejercicioService.validarRespuesta(peticion));
    }
}
