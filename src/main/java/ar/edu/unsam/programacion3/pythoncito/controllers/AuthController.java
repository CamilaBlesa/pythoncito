
package ar.edu.unsam.programacion3.pythoncito.controllers;

import ar.edu.unsam.programacion3.pythoncito.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.unsam.programacion3.pythoncito.dtos.AuthResponseDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.AdminLoginRequestDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.RecuperarAlumnoRequestDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/alumno")
    public ResponseEntity<AuthResponseDTO> ingresarAlumno() {
        String token = authService.crearAlumno();
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/admin")
    public ResponseEntity<AuthResponseDTO> ingresarAdmin(
            @RequestBody AdminLoginRequestDTO peticion) {

        String token = authService.autenticarAdmin(peticion.getClave());

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
    
    @PostMapping("/alumno/recuperar")
    public ResponseEntity<AuthResponseDTO> recuperarAlumno(
        @RequestBody RecuperarAlumnoRequestDTO peticion) {

        String nuevoToken = authService.recuperarAlumno(peticion.getToken());

        return ResponseEntity.ok(new AuthResponseDTO(nuevoToken));
    }
}