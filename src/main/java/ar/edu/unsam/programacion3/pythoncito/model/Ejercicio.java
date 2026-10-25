package ar.edu.unsam.programacion3.pythoncito.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;

@Entity
@Table(name = "Ejercicios")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        description = "Representa un ejercicio de un nivel."
)

public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Identificador único del ejercicio.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(length = 100, nullable = false)
    @Schema(
            description = "Título del ejercicio.",
            example = "Ejercicio 1 - Saludo",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String titulo;

    @Column(length = 100, nullable = false)
    @Schema(
            description = "Tema del ejercicio.",
            example = "Imprimir texto con el método print",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tema;

    @Column(length = 500, nullable = false)
    @Schema(
            description = "Consigna del ejercicio.",
            example = "Ordena los bloques para que python imprima un saludo",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String consigna;

    @Column(length = 1000, nullable = false)
    @Schema(
            description = "Ejemplo diferente del ejercicio.",
            example = "print('Hola, mundo!')",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String ejemplo;

    @Column(length = 1000, nullable = false)
    @Schema(
            description = "Output del ejemplo.",
            example = "Hola, mundo!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String outputEjemplo;

    @Column(length = 1000, nullable = false)
    @Schema(
            description = "Resultado esperado del ejemplo",
            example = "Hola, mundo!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String resultadoEsperadoEjemplo;

    @Column(length = 1000, nullable = false)
    @Schema(
            description = "Bloques disponibles para el ejemplo.",
            example = "print ('Hola, mundo!') ",
            requiredMode = Schema.RequiredMode.REQUIRED
            )
    private String bloquesDisponibles;

    @Column(length = 1000, nullable = false)
    @Schema(
            description = "Solución ordenada del ejemplo.",
            example = "print ('Hola, mundo!') ",
            requiredMode = Schema.RequiredMode.REQUIRED
            )
    private String solucionOrdenada;
}