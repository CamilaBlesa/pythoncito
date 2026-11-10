
package ar.edu.unsam.programacion3.pythoncito.dto;

import java.util.List;

public class EjercicioAdminDTO {

    private Long id;
    private String titulo;
    private String consigna;
    private List<BloqueCodigoAdminDTO> bloques;

    public EjercicioAdminDTO() {
    }

    public EjercicioAdminDTO(
            Long id,
            String titulo,
            String consigna,
            List<BloqueCodigoAdminDTO> bloques) {
        this.id = id;
        this.titulo = titulo;
        this.consigna = consigna;
        this.bloques = bloques;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConsigna() {
        return consigna;
    }

    public void setConsigna(String consigna) {
        this.consigna = consigna;
    }

    public List<BloqueCodigoAdminDTO> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloqueCodigoAdminDTO> bloques) {
        this.bloques = bloques;
    }
}
