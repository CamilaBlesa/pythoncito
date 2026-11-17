package ar.edu.unsam.programacion3.pythoncito.dtos;

public class AdminLoginRequestDTO {

    private String clave;

    public AdminLoginRequestDTO() {
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}