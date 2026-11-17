package ar.edu.unsam.programacion3.pythoncito.dtos;

public class AuthResponseDTO {

    private String token;
    private String identificador;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public AuthResponseDTO(String token, String identificador) {
        this.token = token;
        this.identificador = identificador;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}