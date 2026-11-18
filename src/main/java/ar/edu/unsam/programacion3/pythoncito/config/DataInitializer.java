package ar.edu.unsam.programacion3.pythoncito.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unsam.programacion3.pythoncito.models.*;
import ar.edu.unsam.programacion3.pythoncito.repositories.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NivelRepository nivelRepository;
    private final EjercicioRepository ejercicioRepository;

    public DataInitializer(NivelRepository nivelRepository, EjercicioRepository ejercicioRepository) {
        this.nivelRepository = nivelRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (nivelRepository.count() > 0) {
            return;
        }

        crearNivel1();
        crearNivel2();
        crearNivel3();
        crearNivel4();
        crearNivel5();
    }

    private void crearNivel1() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 1: Variables y Mensajes");
        nivel.setTema("Aprende a guardar datos en variables y mostrarlos por pantalla");
        nivel.setOrdenPresentacion(1);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Mi primer saludo");
        ejercicio.setConsigna("¡Ayudá a Pythoncito a presentarse! Guardá el nombre 'Pythoncito' en una cajita, el saludo '¡Hola ' en otra, y al final usá el loro mágico print(...) para decir el saludo en la pantalla.");
        ejercicio.setExplicacion("Una variable es como una cajita con etiqueta donde guardamos una palabra o número usando el signo igual (=) para no olvidarlo. Y print() es como un loro mágico: todo lo que pongas adentro de sus paréntesis, ¡lo dirá en voz alta y lo mostrará en la pantalla!");
        ejercicio.setEjemploDiferente("juguete = \"Pelota\"\nprint(\"Mi juguete es: \" + juguete)");
        ejercicio.setResultadoEjemplo("Mi juguete es: Pelota");
        ejercicio.setCodigoEsperado("nombre = \"Pythoncito\"\nmensaje = \"¡Hola \"\nprint(mensaje + nombre + \"!\")");
        ejercicio.setOrdenPresentacion(1);
        ejercicio.setNivel(nivel);

        BloqueCodigo b1 = new BloqueCodigo();
        b1.setContenido("nombre = \"Pythoncito\"");
        b1.setOrdenCorrecto(1);
        b1.setOrdenPresentacion(2);
        b1.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b1);

        BloqueCodigo b2 = new BloqueCodigo();
        b2.setContenido("mensaje = \"¡Hola \"");
        b2.setOrdenCorrecto(1);
        b2.setOrdenPresentacion(3);
        b2.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b2);

        BloqueCodigo b3 = new BloqueCodigo();
        b3.setContenido("print(mensaje + nombre + \"!\")");
        b3.setOrdenCorrecto(2);
        b3.setOrdenPresentacion(1);
        b3.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b3);

        Pista p1 = new Pista();
        p1.setTexto("Las dos cajitas (nombre y saludo) tienen que estar listas antes de que el loro print(...) las use.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("No importa si ponés primero la cajita 'nombre' o 'mensaje', ¡las dos opciones antes del print() son correctas!");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel2() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 2: Tomando Decisiones");
        nivel.setTema("Usa condicionales if y else para ejecutar caminos alternativos");
        nivel.setOrdenPresentacion(2);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Aprobado o a Recuperatorio");
        ejercicio.setConsigna("Ordena los bloques para asignar la nota 7 y mostrar 'Aprobado' si la nota es mayor o igual a 6, o 'A recuperar' en caso contrario.");
        ejercicio.setExplicacion("La estructura 'if' evalúa una condición lógica. Si es verdadera se ejecuta su bloque indentado. La cláusula 'else' se ejecuta cuando la condición fue falsa.");
        ejercicio.setEjemploDiferente("puntos = 100\nif puntos > 50:\n    print(\"Ganaste\")\nelse:\n    print(\"Perdiste\")");
        ejercicio.setResultadoEjemplo("Ganaste");
        ejercicio.setCodigoEsperado("nota = 7\nif nota >= 6:\n    print(\"Aprobado\")\nelse:\n    print(\"A recuperar\")");
        ejercicio.setOrdenPresentacion(1);
        ejercicio.setNivel(nivel);

        BloqueCodigo b1 = new BloqueCodigo();
        b1.setContenido("nota = 7");
        b1.setOrdenCorrecto(1);
        b1.setOrdenPresentacion(4);
        b1.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b1);

        BloqueCodigo b2 = new BloqueCodigo();
        b2.setContenido("if nota >= 6:");
        b2.setOrdenCorrecto(2);
        b2.setOrdenPresentacion(2);
        b2.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b2);

        BloqueCodigo b3 = new BloqueCodigo();
        b3.setContenido("    print(\"Aprobado\")");
        b3.setOrdenCorrecto(3);
        b3.setOrdenPresentacion(5);
        b3.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b3);

        BloqueCodigo b4 = new BloqueCodigo();
        b4.setContenido("else:");
        b4.setOrdenCorrecto(4);
        b4.setOrdenPresentacion(1);
        b4.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b4);

        BloqueCodigo b5 = new BloqueCodigo();
        b5.setContenido("    print(\"A recuperar\")");
        b5.setOrdenCorrecto(5);
        b5.setOrdenPresentacion(3);
        b5.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b5);

        Pista p1 = new Pista();
        p1.setTexto("Inicializa la variable nota antes de evaluarla con el if.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("Cada print con cuatro espacios de sangría debe ir inmediatamente después de su correspondiente if o else.");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel3() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 3: Repetición y Bucles");
        nivel.setTema("Aprende a iterar con for y range() para repetir acciones");
        nivel.setOrdenPresentacion(3);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Contador de Despegue");
        ejercicio.setConsigna("Ordena los bloques para imprimir los segundos 1, 2 y 3 usando un bucle for, y finalmente mostrar '¡Despegue!'.");
        ejercicio.setExplicacion("El bucle 'for variable in range(inicio, fin)' itera desde el valor de inicio hasta fin - 1. Las líneas indentadas se repiten en cada iteración.");
        ejercicio.setEjemploDiferente("for i in range(2):\n    print(\"Paso\", i)\nprint(\"Fin\")");
        ejercicio.setResultadoEjemplo("Paso 0\nPaso 1\nFin");
        ejercicio.setCodigoEsperado("for segundo in range(1, 4):\n    print(segundo)\nprint(\"¡Despegue!\")");
        ejercicio.setOrdenPresentacion(1);
        ejercicio.setNivel(nivel);

        BloqueCodigo b1 = new BloqueCodigo();
        b1.setContenido("for segundo in range(1, 4):");
        b1.setOrdenCorrecto(1);
        b1.setOrdenPresentacion(2);
        b1.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b1);

        BloqueCodigo b2 = new BloqueCodigo();
        b2.setContenido("    print(segundo)");
        b2.setOrdenCorrecto(2);
        b2.setOrdenPresentacion(3);
        b2.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b2);

        BloqueCodigo b3 = new BloqueCodigo();
        b3.setContenido("print(\"¡Despegue!\")");
        b3.setOrdenCorrecto(3);
        b3.setOrdenPresentacion(1);
        b3.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b3);

        Pista p1 = new Pista();
        p1.setTexto("La estructura for debe encabezar la secuencia para controlar las repeticiones.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("El bloque print(segundo) va indentado dentro del for, y print('¡Despegue!') va fuera sin indentar.");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel4() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 4: Creando Funciones");
        nivel.setTema("Modulariza lógica definiendo funciones con def y return");
        nivel.setOrdenPresentacion(4);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Función Duplicadora");
        ejercicio.setConsigna("Ordena los bloques para definir la función duplicar(numero), invocarla con el número 5 y mostrar su resultado por pantalla.");
        ejercicio.setExplicacion("Una función se define con la palabra clave def seguida de su nombre y parámetros. La instrucción return entrega el valor calculado al invocador.");
        ejercicio.setEjemploDiferente("def sumar_uno(x):\n    return x + 1\nr = sumar_uno(9)\nprint(r)");
        ejercicio.setResultadoEjemplo("10");
        ejercicio.setCodigoEsperado("def duplicar(numero):\n    return numero * 2\nresultado = duplicar(5)\nprint(resultado)");
        ejercicio.setOrdenPresentacion(1);
        ejercicio.setNivel(nivel);

        BloqueCodigo b1 = new BloqueCodigo();
        b1.setContenido("def duplicar(numero):");
        b1.setOrdenCorrecto(1);
        b1.setOrdenPresentacion(3);
        b1.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b1);

        BloqueCodigo b2 = new BloqueCodigo();
        b2.setContenido("    return numero * 2");
        b2.setOrdenCorrecto(2);
        b2.setOrdenPresentacion(1);
        b2.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b2);

        BloqueCodigo b3 = new BloqueCodigo();
        b3.setContenido("resultado = duplicar(5)");
        b3.setOrdenCorrecto(3);
        b3.setOrdenPresentacion(4);
        b3.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b3);

        BloqueCodigo b4 = new BloqueCodigo();
        b4.setContenido("print(resultado)");
        b4.setOrdenCorrecto(4);
        b4.setOrdenPresentacion(2);
        b4.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b4);

        Pista p1 = new Pista();
        p1.setTexto("Primero debes definir la función con def y su return antes de llamarla.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("Guarda el resultado de duplicar(5) en la variable 'resultado' antes de imprimirla con print().");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel5() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 5: Listas y Colecciones");
        nivel.setTema("Agrupa datos en listas y recórrelas para calcular acumulados");
        nivel.setOrdenPresentacion(5);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Suma de Monedas");
        ejercicio.setConsigna("Ordena los bloques para crear la lista de monedas [10, 20, 50], acumular su suma con un bucle for y mostrar 'Total: 80'.");
        ejercicio.setExplicacion("Una lista almacena varios valores entre corchetes separados por comas. Para acumular la suma, inicializamos una variable en 0 y sumamos cada elemento dentro de un for.");
        ejercicio.setEjemploDiferente("puntos = [5, 5]\ntotal = 0\nfor p in puntos:\n    total = total + p\nprint(total)");
        ejercicio.setResultadoEjemplo("10");
        ejercicio.setCodigoEsperado("monedas = [10, 20, 50]\ntotal = 0\nfor m in monedas:\n    total = total + m\nprint(\"Total:\", total)");
        ejercicio.setOrdenPresentacion(1);
        ejercicio.setNivel(nivel);

        BloqueCodigo b1 = new BloqueCodigo();
        b1.setContenido("monedas = [10, 20, 50]");
        b1.setOrdenCorrecto(1);
        b1.setOrdenPresentacion(4);
        b1.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b1);

        BloqueCodigo b2 = new BloqueCodigo();
        b2.setContenido("total = 0");
        b2.setOrdenCorrecto(2);
        b2.setOrdenPresentacion(1);
        b2.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b2);

        BloqueCodigo b3 = new BloqueCodigo();
        b3.setContenido("for m in monedas:");
        b3.setOrdenCorrecto(3);
        b3.setOrdenPresentacion(5);
        b3.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b3);

        BloqueCodigo b4 = new BloqueCodigo();
        b4.setContenido("    total = total + m");
        b4.setOrdenCorrecto(4);
        b4.setOrdenPresentacion(2);
        b4.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b4);

        BloqueCodigo b5 = new BloqueCodigo();
        b5.setContenido("print(\"Total:\", total)");
        b5.setOrdenCorrecto(5);
        b5.setOrdenPresentacion(3);
        b5.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b5);

        Pista p1 = new Pista();
        p1.setTexto("Crea la lista y la variable total en 0 antes de iniciar el for.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("La suma acumulativa va dentro del for indentada, y el print mostrando el total va al final.");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }
}
