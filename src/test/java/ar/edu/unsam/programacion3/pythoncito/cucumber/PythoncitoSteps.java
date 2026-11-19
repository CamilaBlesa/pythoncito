package ar.edu.unsam.programacion3.pythoncito.cucumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.jayway.jsonpath.JsonPath;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PythoncitoSteps {

    private record Solucion(long ejercicioId, List<Long> bloquesEnOrden) {
    }

    @Autowired
    private MockMvc mockMvc;

    @Value("${pythoncito.security.admin-token}")
    private String claveAdmin;

    private String token;
    private String tokenAdminCache;
    private MvcResult respuesta;
    private Long nivelCreadoId;

    // ---------- helpers ----------

    private void ejecutar(MockHttpServletRequestBuilder peticion, String bearer) throws Exception {
        if (bearer != null) {
            peticion.header("Authorization", "Bearer " + bearer);
        }
        respuesta = mockMvc.perform(peticion).andReturn();
    }

    private void enviar(MockHttpServletRequestBuilder peticion) throws Exception {
        ejecutar(peticion, token);
    }

    private MockHttpServletRequestBuilder conJson(MockHttpServletRequestBuilder peticion, String cuerpo) {
        return peticion.contentType(MediaType.APPLICATION_JSON).content(cuerpo);
    }

    private String cuerpo() throws Exception {
        return respuesta.getResponse().getContentAsString();
    }

    private String leerToken() throws Exception {
        return JsonPath.read(cuerpo(), "$.token");
    }

    private MockHttpServletRequestBuilder loginAdmin(String clave) {
        return conJson(post("/api/auth/admin"), "{\"clave\":\"" + clave + "\"}");
    }

    private String tokenAdmin() throws Exception {
        if (tokenAdminCache == null) {
            ejecutar(loginAdmin(claveAdmin), null);
            tokenAdminCache = leerToken();
        }
        return tokenAdminCache;
    }

    private long idNivel(int posicion) throws Exception {
        ejecutar(get("/api/estudiante/niveles"), token);
        Number id = JsonPath.read(cuerpo(), "$[" + (posicion - 1) + "].id");
        return id.longValue();
    }

    /** Busca el ejercicio del nivel y devuelve sus bloques en el orden correcto (usa la API admin). */
    @SuppressWarnings("unchecked")
    private Solucion solucionDelNivel(int posicion) throws Exception {
        long nivelId = idNivel(posicion);
        ejecutar(get("/api/admin/ejercicios"), tokenAdmin());
        List<Map<String, Object>> ejercicios = JsonPath.read(cuerpo(), "$");

        for (Map<String, Object> ejercicio : ejercicios) {
            if (((Number) ejercicio.get("nivelId")).longValue() == nivelId) {
                ArrayList<Map<String, Object>> bloques = new ArrayList<>((List<Map<String, Object>>) ejercicio.get("bloques"));
                bloques.sort(Comparator.comparingInt(b -> ((Number) b.get("ordenCorrecto")).intValue()));

                List<Long> ids = new ArrayList<>();
                for (Map<String, Object> bloque : bloques) {
                    ids.add(((Number) bloque.get("id")).longValue());
                }
                return new Solucion(((Number) ejercicio.get("id")).longValue(), ids);
            }
        }
        throw new IllegalStateException("No hay ejercicios en el nivel " + posicion);
    }

    private void validar(Solucion solucion, List<Long> bloques) throws Exception {
        enviar(conJson(
                post("/api/estudiante/ejercicios/" + solucion.ejercicioId() + "/validar"),
                bloques.toString()));
    }

    // ---------- autenticación ----------

    @Given("un visitante sin autenticar")
    public void visitanteSinAutenticar() {
        token = null;
    }

    @Given("un estudiante autenticado")
    public void estudianteAutenticado() throws Exception {
        ejecutar(post("/api/auth/estudiante"), null);
        token = leerToken();
    }

    @Given("un administrador autenticado")
    public void administradorAutenticado() throws Exception {
        ejecutar(loginAdmin(claveAdmin), null);
        token = leerToken();
    }

    @When("ingreso como estudiante nuevo")
    public void ingresoComoEstudianteNuevo() throws Exception {
        estudianteAutenticado();
    }

    @When("ingreso como administrador con la clave correcta")
    public void ingresoAdminClaveCorrecta() throws Exception {
        ejecutar(loginAdmin(claveAdmin), null);
    }

    @When("ingreso como administrador con la clave {string}")
    public void ingresoAdminConClave(String clave) throws Exception {
        ejecutar(loginAdmin(clave), null);
    }

    @When("recupero la sesión con mi token actual")
    public void recuperoSesion() throws Exception {
        ejecutar(conJson(post("/api/auth/estudiante/recuperar"), "{\"token\":\"" + token + "\"}"), null);
    }

    // ---------- peticiones genéricas ----------

    @When("hago un GET a {string}")
    public void peticionGet(String url) throws Exception {
        enviar(get(url));
    }

    // ---------- niveles y progreso ----------

    @Then("el nivel {int} está desbloqueado")
    public void nivelDesbloqueado(int posicion) throws Exception {
        ejecutar(get("/api/estudiante/niveles"), token);
        Object valor = JsonPath.read(cuerpo(), "$[" + (posicion - 1) + "].desbloqueado");
        assertThat(String.valueOf(valor)).isEqualTo("true");
    }

    @Then("el nivel {int} está bloqueado")
    public void nivelBloqueado(int posicion) throws Exception {
        ejecutar(get("/api/estudiante/niveles"), token);
        Object valor = JsonPath.read(cuerpo(), "$[" + (posicion - 1) + "].desbloqueado");
        assertThat(String.valueOf(valor)).isEqualTo("false");
    }

    @When("intento abrir el nivel {int}")
    public void intentoAbrirNivel(int posicion) throws Exception {
        enviar(get("/api/estudiante/niveles/" + idNivel(posicion)));
    }

    @When("consulto los ejercicios del nivel {int}")
    public void consultoEjerciciosDelNivel(int posicion) throws Exception {
        enviar(get("/api/estudiante/niveles/" + idNivel(posicion) + "/ejercicios"));
    }

    @When("consulto mi progreso")
    public void consultoProgreso() throws Exception {
        enviar(get("/api/estudiante/progreso"));
    }

    // ---------- validación, intentos y pistas ----------

    @When("resuelvo correctamente el ejercicio del nivel {int}")
    public void resuelvoCorrectamente(int posicion) throws Exception {
        Solucion solucion = solucionDelNivel(posicion);
        validar(solucion, solucion.bloquesEnOrden());
    }

    @When("envío los bloques del ejercicio del nivel {int} en orden inverso")
    public void envioEnOrdenInverso(int posicion) throws Exception {
        Solucion solucion = solucionDelNivel(posicion);
        List<Long> bloques = new ArrayList<>(solucion.bloquesEnOrden());
        Collections.reverse(bloques);
        validar(solucion, bloques);
    }

    @When("envío los bloques del ejercicio del nivel {int} sin el último bloque")
    public void envioSinElUltimo(int posicion) throws Exception {
        Solucion solucion = solucionDelNivel(posicion);
        List<Long> bloques = new ArrayList<>(solucion.bloquesEnOrden());
        bloques.remove(bloques.size() - 1);
        validar(solucion, bloques);
    }

    @When("envío los bloques del ejercicio del nivel {int} con un bloque ajeno")
    public void envioConBloqueAjeno(int posicion) throws Exception {
        Solucion solucion = solucionDelNivel(posicion);
        List<Long> bloques = new ArrayList<>(solucion.bloquesEnOrden());
        bloques.add(999999L);
        validar(solucion, bloques);
    }

    @When("consulto los intentos del ejercicio del nivel {int}")
    public void consultoIntentos(int posicion) throws Exception {
        Solucion solucion = solucionDelNivel(posicion);
        enviar(get("/api/estudiante/ejercicios/" + solucion.ejercicioId() + "/intentos"));
    }

    @When("pido la pista {int} del ejercicio del nivel {int}")
    public void pidoPista(int orden, int posicion) throws Exception {
        Solucion solucion = solucionDelNivel(posicion);
        enviar(get("/api/estudiante/ejercicios/" + solucion.ejercicioId() + "/pistas/" + orden));
    }

    // ---------- administración ----------

    @When("creo un nivel con título {string}")
    public void creoNivel(String titulo) throws Exception {
        String json = "{\"titulo\":\"" + titulo + "\",\"tema\":\"Tema de prueba\","
                + "\"activo\":true,\"ordenPresentacion\":99}";
        enviar(conJson(post("/api/admin/niveles"), json));
        Number id = JsonPath.read(cuerpo(), "$.id");
        nivelCreadoId = id.longValue();
    }

    @When("doy de baja el nivel creado")
    public void doyDeBajaNivelCreado() throws Exception {
        enviar(patch("/api/admin/niveles/" + nivelCreadoId + "/baja"));
    }

    // ---------- verificaciones de la respuesta ----------

    @Then("la respuesta tiene estado {int}")
    public void respuestaTieneEstado(int esperado) {
        assertThat(respuesta.getResponse().getStatus()).isEqualTo(esperado);
    }


    @Then("la respuesta es exitosa")
    public void respuestaExitosa() {
        assertThat(respuesta.getResponse().getStatus()).isBetween(200, 299);
    }

    @Then("la respuesta contiene un token")
    public void respuestaContieneToken() throws Exception {
        assertThat(leerToken()).isNotBlank();
    }

    @Then("el campo {string} de la respuesta es {string}")
    public void campoDeLaRespuesta(String ruta, String esperado) throws Exception {
        Object valor = JsonPath.read(cuerpo(), ruta);
        assertThat(String.valueOf(valor)).isEqualTo(esperado);
    }

    @Then("la respuesta tiene {int} elemento(s)")
    public void respuestaTieneElementos(int cantidad) throws Exception {
        List<?> lista = JsonPath.read(cuerpo(), "$");
        assertThat(lista).hasSize(cantidad);
    }

    @Then("la respuesta tiene al menos {int} elementos")
    public void respuestaTieneAlMenos(int cantidad) throws Exception {
        List<?> lista = JsonPath.read(cuerpo(), "$");
        assertThat(lista).hasSizeGreaterThanOrEqualTo(cantidad);
    }
}