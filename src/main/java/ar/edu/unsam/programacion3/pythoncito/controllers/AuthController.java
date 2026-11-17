
package ar.edu.unsam.programacion3.pythoncito.controllers;

import ar.edu.unsam.programacion3.pythoncito.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.unsam.programacion3.pythoncito.dtos.AuthResponseDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.AdminLoginRequestDTO;
import ar.edu.unsam.programacion3.pythoncito.dtos.RecuperarEstudianteRequestDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/estudiante")
    public ResponseEntity<AuthResponseDTO> ingresarEstudiante() {
        String token = authService.crearEstudiante();
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/admin")
    public ResponseEntity<AuthResponseDTO> ingresarAdmin(
            @RequestBody AdminLoginRequestDTO peticion) {

        String token = authService.autenticarAdmin(peticion.getClave());

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
    
    @PostMapping("/estudiante/recuperar")
    public ResponseEntity<AuthResponseDTO> recuperarEstudiante(
        @RequestBody RecuperarEstudianteRequestDTO peticion) {

        String nuevoToken = authService.recuperarEstudiante(peticion.getToken());

        return ResponseEntity.ok(new AuthResponseDTO(nuevoToken));
    }
}