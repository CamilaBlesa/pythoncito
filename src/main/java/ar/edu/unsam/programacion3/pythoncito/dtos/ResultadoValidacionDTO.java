package ar.edu.unsam.programacion3.pythoncito.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resultado de la validacion de una respuesta.")
public class ResultadoValidacionDTO {

    private Boolean correcta;
    private String mensaje;
    private List<Long> bloquesFaltantes;
    private List<Long> bloquesSobrantes;
    private List<Long> bloquesFueraDeOrden;

    public ResultadoValidacionDTO() {
    }

    public ResultadoValidacionDTO(Boolean correcta, String mensaje,
                                  List<Long> bloquesFaltantes,
                                  List<Long> bloquesSobrantes,
                                  List<Long> bloquesFueraDeOrden) {
        this.correcta = correcta;
        this.mensaje = mensaje;
        this.bloquesFaltantes = bloquesFaltantes;
        this.bloquesSobrantes = bloquesSobrantes;
        this.bloquesFueraDeOrden = bloquesFueraDeOrden;
    }

    public Boolean getCorrecta() {
        return correcta;
    }

    public void setCorrecta(Boolean correcta) {
        this.correcta = correcta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<Long> getBloquesFaltantes() {
        return bloquesFaltantes;
    }

    public void setBloquesFaltantes(List<Long> bloquesFaltantes) {
        this.bloquesFaltantes = bloquesFaltantes;
    }

    public List<Long> getBloquesSobrantes() {
        return bloquesSobrantes;
    }

    public void setBloquesSobrantes(List<Long> bloquesSobrantes) {
        this.bloquesSobrantes = bloquesSobrantes;
    }

    public List<Long> getBloquesFueraDeOrden() {
        return bloquesFueraDeOrden;
    }

    public void setBloquesFueraDeOrden(List<Long> bloquesFueraDeOrden) {
        this.bloquesFueraDeOrden = bloquesFueraDeOrden;
    }
}
