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
        ejercicio.setConsigna("¡Ayudá a Pythoncito a calificar un examen! Primero guardá la nota 7 en su cajita. Luego preguntale a la compu: si la nota es 6 o más (nota >= 6), el loro dirá 'Aprobado'. Y si no (else:), dirá 'A recuperar'.");
        ejercicio.setExplicacion("¡En la vida tomamos decisiones todo el tiempo! Por ejemplo: 'Si (if) llueve, llevo paraguas; si no (else), salgo con lentes'. En programación, el 'if' es como hacerle una pregunta a la compu. Si la respuesta es sí, la compu ejecuta la orden que está un pasito adentro (con espacios a la derecha). Y si la respuesta es no, ¡hace lo que está adentro del 'else'! ¡Es como elegir qué camino tomar en un videojuego!");
        ejercicio.setEjemploDiferente("energia = 100\nif energia > 50:\n    print(\"¡Listo para jugar!\")\nelse:\n    print(\"A descansar\")");
        ejercicio.setResultadoEjemplo("¡Listo para jugar!");
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
        p1.setTexto("Primero tenés que crear la cajita 'nota = 7' antes de que el 'if' pueda fijarse qué nota sacó.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("Fijate que los bloques de print(...) tienen espacios a la izquierda (están un pasito adentro). Cada print va justo abajo de su 'if' o de su 'else'.");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel3() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 3: Repetición y Bucles");
        nivel.setTema("Aprende a usar el bucle for para repetir acciones como un reloj");
        nivel.setOrdenPresentacion(3);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Contador de Despegue");
        ejercicio.setConsigna("¡Ayudá a Pythoncito a lanzar un cohete al espacio! Usá un bucle for para contar los segundos 1, 2 y 3 en voz alta, y cuando termine la cuenta regresiva, hacé que grite '¡Despegue!'.");
        ejercicio.setExplicacion("A veces queremos que la compu repita una acción muchas veces sin tener que escribirla una y otra vez. Para eso usamos el bucle 'for' con 'range(...)'. Es como una calesita o un reloj: da vueltas contando números. Todo lo que pongas un pasito adentro (con espacios a la derecha) se repetirá en cada vuelta. ¡Y lo que pongas afuera se ejecutará recién al final!");
        ejercicio.setEjemploDiferente("for aplauso in range(1, 3):\n    print(\"¡Aplauso!\", aplauso)\nprint(\"¡Bravo!\")");
        ejercicio.setResultadoEjemplo("¡Aplauso! 1\n¡Aplauso! 2\n¡Bravo!");
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
        p1.setTexto("Primero va el bloque del 'for' para encender la cuenta de los segundos.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("Fijate que print(segundo) tiene espacios y va un pasito adentro porque se repite en cada vuelta. En cambio, print('¡Despegue!') va afuera de la calesita, abajo del todo.");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel4() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 4: Creando Funciones");
        nivel.setTema("Construye tus propias máquinas mágicas con def y return");
        nivel.setOrdenPresentacion(4);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Función Duplicadora");
        ejercicio.setConsigna("¡Ayudá a Pythoncito a construir una máquina mágica que duplica números! Creá la máquina 'duplicar(numero)' con def, hacé que devuelva el doble con return, usala con el número 5 y mostrá el resultado en pantalla.");
        ejercicio.setExplicacion("Una función con 'def' es como fabricar tu propia máquina mágica o un robot ayudante: le ponés un nombre, le das un ingrediente (parámetro), y con 'return' la máquina te devuelve el premio terminado. Una vez que tu máquina está construida, ¡podés llamarla y usarla todas las veces que quieras con distintos números!");
        ejercicio.setEjemploDiferente("def sumar_uno(x):\n    return x + 1\npremio = sumar_uno(9)\nprint(premio)");
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
        p1.setTexto("Primero tenés que armar la maquinita con 'def' y su 'return' antes de poder usarla.");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("Una vez armada la máquina, llamala con duplicar(5), guardá el premio en la cajita 'resultado' y mostralo con print(resultado).");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }

    private void crearNivel5() {
        Nivel nivel = new Nivel();
        nivel.setTitulo("Nivel 5: Listas y Colecciones");
        nivel.setTema("Guarda tesoros en listas y recórrelos para sumar tus ahorros");
        nivel.setOrdenPresentacion(5);
        nivel.setActivo(true);
        nivel = nivelRepository.save(nivel);

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo("Suma de Monedas");
        ejercicio.setConsigna("¡Pythoncito abrió su alcancía! Guardá las monedas [10, 20, 50] en una lista, sumalas una por una con un bucle for en una cajita 'total', y al final mostrá el total ahorrado en la pantalla.");
        ejercicio.setExplicacion("Una lista entre corchetes [...] es como una cartuchera o una alcancía: ¡te permite guardar muchos elementos juntos en un solo lugar! Y si usamos un bucle 'for m in monedas:', es como sacar las monedas de la alcancía una por una para ir sumándolas a nuestro chanchito de ahorros.");
        ejercicio.setEjemploDiferente("caramelos = [5, 5]\nbolsa = 0\nfor c in caramelos:\n    bolsa = bolsa + c\nprint(\"Total:\", bolsa)");
        ejercicio.setResultadoEjemplo("Total: 10");
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
        b2.setOrdenCorrecto(1);
        b2.setOrdenPresentacion(1);
        b2.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b2);

        BloqueCodigo b3 = new BloqueCodigo();
        b3.setContenido("for m in monedas:");
        b3.setOrdenCorrecto(2);
        b3.setOrdenPresentacion(5);
        b3.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b3);

        BloqueCodigo b4 = new BloqueCodigo();
        b4.setContenido("    total = total + m");
        b4.setOrdenCorrecto(3);
        b4.setOrdenPresentacion(2);
        b4.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b4);

        BloqueCodigo b5 = new BloqueCodigo();
        b5.setContenido("print(\"Total:\", total)");
        b5.setOrdenCorrecto(4);
        b5.setOrdenPresentacion(3);
        b5.setEjercicio(ejercicio);
        ejercicio.getBloques().add(b5);

        Pista p1 = new Pista();
        p1.setTexto("Antes de ponerte a contar, tenés que tener lista la lista de monedas y la cajita 'total = 0' en cero (¡pueden ir en cualquier orden!).");
        p1.setOrden(1);
        p1.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p1);

        Pista p2 = new Pista();
        p2.setTexto("La suma de cada moneda va un pasito adentro del for, y el loro print con el total va afuera, al final de todo.");
        p2.setOrden(2);
        p2.setEjercicio(ejercicio);
        ejercicio.getPistas().add(p2);

        ejercicioRepository.save(ejercicio);
    }
}
