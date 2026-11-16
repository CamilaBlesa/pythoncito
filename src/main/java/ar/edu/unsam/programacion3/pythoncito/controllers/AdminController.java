package ar.edu.unsam.programacion3.pythoncito.controllers;

import ar.edu.unsam.programacion3.pythoncito.dtos.BloqueCodigoAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.EjercicioAdminDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.NivelDTO;
import ar.edu.unsam.programacion3.pythoncito.services.BloqueCodigoService;
import ar.edu.unsam.programacion3.pythoncito.services.EjercicioService;
import ar.edu.unsam.programacion3.pythoncito.services.NivelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final NivelService nivelService;
    private final EjercicioService ejercicioService;
    private final BloqueCodigoService bloqueCodigoService;

    public AdminController(NivelService nivelService,
                           EjercicioService ejercicioService,
                           BloqueCodigoService bloqueCodigoService) {
        this.nivelService = nivelService;
        this.ejercicioService = ejercicioService;
        this.bloqueCodigoService = bloqueCodigoService;
    }

    @GetMapping("/niveles")
    public ResponseEntity<List<NivelDTO>> listarNiveles() {
        return ResponseEntity.ok(nivelService.listar());
    }

    @PostMapping("/niveles")
    public ResponseEntity<NivelDTO> crearNivel(@RequestBody NivelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nivelService.crear(dto));
    }

    @PutMapping("/niveles/{id}")
    public ResponseEntity<NivelDTO> modificarNivel(@PathVariable Long id, @RequestBody NivelDTO dto) {
        return ResponseEntity.ok(nivelService.modificar(id, dto));
    }

    @PatchMapping("/niveles/{id}/baja")
    public ResponseEntity<NivelDTO> darDeBajaNivel(@PathVariable Long id) {
        return ResponseEntity.ok(nivelService.darDeBaja(id));
    }

    @GetMapping("/ejercicios")
    public ResponseEntity<List<EjercicioAdminDTO>> listarEjercicios() {
        return ResponseEntity.ok(ejercicioService.listarParaAdmin());
    }

    @GetMapping("/ejercicios/{id}")
    public ResponseEntity<EjercicioAdminDTO> obtenerEjercicio(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.obtenerEjercicioParaAdmin(id));
    }

    @PostMapping("/ejercicios")
    public ResponseEntity<EjercicioAdminDTO> crearEjercicio(@RequestBody EjercicioAdminDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ejercicioService.crear(dto));
    }

    @PutMapping("/ejercicios/{id}")
    public ResponseEntity<EjercicioAdminDTO> modificarEjercicio(@PathVariable Long id, @RequestBody EjercicioAdminDTO dto) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, dto));
    }

    @DeleteMapping("/ejercicios/{id}")
    public ResponseEntity<Void> eliminarEjercicio(@PathVariable Long id) {
        ejercicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bloques/{id}")
    public ResponseEntity<BloqueCodigoAdminDTO> obtenerBloque(@PathVariable Long id) {
        return ResponseEntity.ok(bloqueCodigoService.obtenerPorId(id));
    }

    @PutMapping("/bloques/{id}")
    public ResponseEntity<BloqueCodigoAdminDTO> modificarBloque(@PathVariable Long id, @RequestBody BloqueCodigoAdminDTO dto) {
        return ResponseEntity.ok(bloqueCodigoService.modificar(id, dto));
    }
}
