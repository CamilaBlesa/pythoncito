let adminToken = sessionStorage.getItem("pythoncito_admin_token");

let nivelSeleccionadoId = null;
let tituloNivelSeleccionado = "";
let nivelesAdmin = [];

// INICIALIZACIÓN

document.addEventListener("DOMContentLoaded", () => {
  inicializarPreferenciasAccesibilidad();

  configurarEventosAdmin();

  if (adminToken) {
    mostrarDashboard();
  } else {
    mostrarLogin();
  }
});

// CONFIGURACIÓN DE EVENTOS

function configurarEventosAdmin() {
  // Iniciar sesión
  const loginForm = document.getElementById("admin-login-form");

  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const clave = document.getElementById("admin-clave").value;

      await autenticarAdmin(clave);
    });
  }

  // Cerrar sesión
  const btnLogout = document.getElementById("btn-admin-logout");

  if (btnLogout) {
    btnLogout.addEventListener("click", () => {
      sessionStorage.removeItem("pythoncito_admin_token");
      adminToken = null;

      mostrarLogin();
    });
  }

  // Crear nivel
  const btnNuevoNivel = document.getElementById("btn-nuevo-nivel");

  if (btnNuevoNivel) {
    btnNuevoNivel.addEventListener("click", () => {
      abrirModalNivel();
    });
  }

  // Cancelar modal de nivel
  const btnCancelarModal = document.getElementById("btn-cancelar-modal");

  if (btnCancelarModal) {
    btnCancelarModal.addEventListener("click", cerrarModalNivel);
  }

  // Guardar nivel
  const formNivel = document.getElementById("form-nivel");

  if (formNivel) {
    formNivel.addEventListener("submit", async (e) => {
      e.preventDefault();

      await guardarNivel();
    });
  }

  // Volver al listado de niveles
  const btnVolverNiveles = document.getElementById("btn-volver-niveles");

  if (btnVolverNiveles) {
    btnVolverNiveles.addEventListener("click", () => {
      mostrarSeccionNiveles();
    });
  }

  const btnNuevoEjercicio = document.getElementById("btn-nuevo-ejercicio");

  if (btnNuevoEjercicio) {
    btnNuevoEjercicio.addEventListener("click", () => {
      abrirModalEjercicio();
    });
  }

  const btnCancelarEjercicio = document.getElementById(
    "btn-cancelar-ejercicio",
  );

  if (btnCancelarEjercicio) {
    btnCancelarEjercicio.addEventListener("click", cerrarModalEjercicio);
  }

  const formEjercicio = document.getElementById("form-ejercicio");

  if (formEjercicio) {
    formEjercicio.addEventListener("submit", async (e) => {
      e.preventDefault();
      await guardarEjercicio();
    });
  }
}

// AUTENTICACIÓN

async function autenticarAdmin(clave) {
  const errorBox = document.getElementById("login-error");

  errorBox.style.display = "none";

  try {
    const respuesta = await fetch("/api/auth/admin", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ clave }),
    });

    if (!respuesta.ok) {
      throw new Error("Clave docente incorrecta.");
    }

    const data = await respuesta.json();

    adminToken = data.token;

    sessionStorage.setItem("pythoncito_admin_token", adminToken);

    mostrarDashboard();
  } catch (error) {
    errorBox.textContent = error.message;
    errorBox.style.display = "block";
  }
}

// MOSTRAR SECCIONES

function mostrarLogin() {
  document.getElementById("login-section").style.display = "block";

  document.getElementById("dashboard-section").style.display = "none";

  document.getElementById("ejercicios-section").style.display = "none";

  cerrarModalNivel();
  cerrarModalEjercicio();
}

function mostrarDashboard() {
  document.getElementById("login-section").style.display = "none";

  document.getElementById("dashboard-section").style.display = "block";

  document.getElementById("ejercicios-section").style.display = "none";

  cargarNivelesAdmin();
}

function mostrarSeccionNiveles() {
  document.getElementById("ejercicios-section").style.display = "none";

  document.getElementById("dashboard-section").style.display = "block";
}

// PETICIONES AL BACKEND

async function adminFetch(endpoint, opciones = {}) {
  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${adminToken}`,
    ...(opciones.headers || {}),
  };

  const respuesta = await fetch(`/api${endpoint}`, {
    ...opciones,
    headers,
  });

  // Token vencido o sin autorización
  if (respuesta.status === 401 || respuesta.status === 403) {
    sessionStorage.removeItem("pythoncito_admin_token");

    adminToken = null;

    mostrarLogin();

    throw new Error("Tu sesión expiró o no tenés autorización.");
  }

  // Respuestas con error
  if (!respuesta.ok) {
    const contenido = await respuesta.text();

    let mensaje = `Error HTTP ${respuesta.status}`;

    if (contenido) {
      try {
        const error = JSON.parse(contenido);

        mensaje = error.mensaje || error.message || error.error || mensaje;
      } catch {
        mensaje = contenido;
      }
    }

    throw new Error(mensaje);
  }

  // Respuesta sin contenido
  if (respuesta.status === 204) {
    return null;
  }

  const contenido = await respuesta.text();

  if (!contenido) {
    return null;
  }

  try {
    return JSON.parse(contenido);
  } catch {
    throw new Error("El servidor devolvió una respuesta JSON inválida.");
  }
}

// LISTADO DE NIVELES

async function cargarNivelesAdmin() {
  const tbody = document.getElementById("niveles-tbody");

  tbody.textContent = "Cargando niveles...";

  try {
    nivelesAdmin = await adminFetch("/admin/niveles");

    tbody.innerHTML = "";

    if (!nivelesAdmin || nivelesAdmin.length === 0) {
      const fila = document.createElement("tr");
      const celda = document.createElement("td");

      celda.colSpan = 6;
      celda.textContent = "No hay niveles registrados.";
      celda.style.textAlign = "center";

      fila.appendChild(celda);
      tbody.appendChild(fila);

      return;
    }

    for (let i = 0; i < nivelesAdmin.length; i++) {
      const nivel = nivelesAdmin[i];

      const fila = document.createElement("tr");

      // ID
      const tdId = document.createElement("td");
      tdId.textContent = nivel.id;

      // Orden
      const tdOrden = document.createElement("td");
      tdOrden.textContent = nivel.ordenPresentacion;

      // Título
      const tdTitulo = document.createElement("td");
      tdTitulo.textContent = nivel.titulo;

      // Tema
      const tdTema = document.createElement("td");
      tdTema.textContent = nivel.tema;

      // Estado
      const tdEstado = document.createElement("td");
      tdEstado.textContent = nivel.activo ? "Activo" : "Inactivo";

      // Acciones
      const tdAcciones = document.createElement("td");
      tdAcciones.className = "admin-actions";

      // Modificar nivel
      const btnEditar = document.createElement("button");

      btnEditar.type = "button";
      btnEditar.className = "btn-secondary";
      btnEditar.textContent = "Modificar";

      btnEditar.addEventListener("click", () => {
        abrirModalNivel(
          nivel.id,
          nivel.titulo,
          nivel.tema,
          nivel.ordenPresentacion,
          nivel.activo,
        );
      });

      tdAcciones.appendChild(btnEditar);

      // Ver ejercicios
      const btnEjercicios = document.createElement("button");

      btnEjercicios.type = "button";
      btnEjercicios.className = "btn-secondary";
      btnEjercicios.textContent = "Ver ejercicios";

      btnEjercicios.addEventListener("click", () => {
        console.log("Clic en Ver ejercicios");
        console.log("ID del nivel:", nivel.id);
        console.log("Título:", nivel.titulo);

        cargarEjerciciosAdmin(nivel.id, nivel.titulo);
      });

      tdAcciones.appendChild(btnEjercicios);

      // Dar de baja
      if (nivel.activo) {
        const btnBaja = document.createElement("button");

        btnBaja.type = "button";
        btnBaja.className = "btn-secondary";
        btnBaja.textContent = "Dar de baja";

        btnBaja.addEventListener("click", () => {
          darDeBajaNivel(nivel.id);
        });

        tdAcciones.appendChild(btnBaja);
      }

      fila.append(tdId, tdOrden, tdTitulo, tdTema, tdEstado, tdAcciones);

      tbody.appendChild(fila);
    }
  } catch (error) {
    tbody.innerHTML = "";

    const fila = document.createElement("tr");
    const celda = document.createElement("td");

    celda.colSpan = 6;
    celda.textContent = error.message;
    celda.style.textAlign = "center";

    fila.appendChild(celda);
    tbody.appendChild(fila);
  }
}

// DAR DE BAJA UN NIVEL

async function darDeBajaNivel(id) {
  const confirmar = window.confirm(
    "¿Estás seguro de que deseas dar de baja este nivel?",
  );

  if (!confirmar) {
    return;
  }

  try {
    await adminFetch(`/admin/niveles/${id}/baja`, {
      method: "PATCH",
    });

    await cargarNivelesAdmin();
  } catch (error) {
    alert("No se pudo dar de baja el nivel: " + error.message);
  }
}

// CREAR Y MODIFICAR NIVELES

function abrirModalNivel(
  id = null,
  titulo = "",
  tema = "",
  orden = 1,
  activo = true,
) {
  document.getElementById("nivel-id").value = id || "";

  document.getElementById("nivel-titulo").value = titulo;

  document.getElementById("nivel-tema").value = tema;

  document.getElementById("nivel-orden").value = orden;

  document.getElementById("nivel-activo").checked = activo;

  document.getElementById("modal-title").textContent = id
    ? "Modificar Nivel"
    : "Crear Nivel";

  document.getElementById("modal-nivel").classList.add("show");
}

function cerrarModalNivel() {
  document.getElementById("modal-nivel").classList.remove("show");
}

async function guardarNivel() {
  const id = document.getElementById("nivel-id").value;

  const payload = {
    titulo: document.getElementById("nivel-titulo").value.trim(),

    tema: document.getElementById("nivel-tema").value.trim(),

    ordenPresentacion: Number(document.getElementById("nivel-orden").value),

    activo: document.getElementById("nivel-activo").checked,
  };

  try {
    if (id) {
      await adminFetch(`/admin/niveles/${id}`, {
        method: "PUT",
        body: JSON.stringify(payload),
      });
    } else {
      await adminFetch("/admin/niveles", {
        method: "POST",
        body: JSON.stringify(payload),
      });
    }

    cerrarModalNivel();

    await cargarNivelesAdmin();
  } catch (error) {
    alert("Error al guardar nivel: " + error.message);
  }
}

// LISTADO DE EJERCICIOS DE UN NIVEL
async function cargarEjerciciosAdmin(nivelId, tituloNivel) {
  nivelSeleccionadoId = nivelId;
  tituloNivelSeleccionado = tituloNivel;

  document.getElementById("dashboard-section").style.display = "none";

  document.getElementById("ejercicios-section").style.display = "block";

  const tbody = document.getElementById("ejercicios-tbody");

  try {
    const ejercicios = await adminFetch("/admin/ejercicios");

    tbody.innerHTML = "";

    const ejerciciosDelNivel = ejercicios.filter(
      (ejercicio) => Number(ejercicio.nivelId) === Number(nivelId),
    );

    if (ejerciciosDelNivel.length === 0) {
      tbody.innerHTML = `
                <tr>
                    <td colspan="4">
                        Este nivel todavía no tiene ejercicios.
                    </td>
                </tr>`;
      return;
    }

    for (let i = 0; i < ejerciciosDelNivel.length; i++) {
      const ejercicio = ejerciciosDelNivel[i];

      const fila = document.createElement("tr");

      const tdId = document.createElement("td");
      tdId.textContent = ejercicio.id;

      const tdOrden = document.createElement("td");
      tdOrden.textContent = ejercicio.ordenPresentacion;

      const tdTitulo = document.createElement("td");
      tdTitulo.textContent = ejercicio.titulo;

      const tdAcciones = document.createElement("td");
      tdAcciones.className = "admin-actions";

      const btnModificar = document.createElement("button");
      btnModificar.type = "button";
      btnModificar.className = "btn-secondary";
      btnModificar.textContent = "Modificar";

      btnModificar.addEventListener("click", () => {
        abrirModalEjercicio(ejercicio.id);
      });

      const btnEliminar = document.createElement("button");
      btnEliminar.type = "button";
      btnEliminar.className = "btn-secondary";
      btnEliminar.textContent = "Eliminar";

      btnEliminar.addEventListener("click", () => {
        eliminarEjercicio(ejercicio.id);
      });

      tdAcciones.appendChild(btnModificar);
      tdAcciones.appendChild(btnEliminar);

      fila.appendChild(tdId);
      fila.appendChild(tdOrden);
      fila.appendChild(tdTitulo);
      fila.appendChild(tdAcciones);

      tbody.appendChild(fila);
    }
  } catch (error) {
    tbody.innerHTML = `
            <tr>
                <td colspan="4">${error.message}</td>
            </tr>`;
  }
}

// OBTENER UN EJERCICIO Y ABRIR EL MODAL
async function abrirModalEjercicio(id = null) {
  const formulario = document.getElementById("form-ejercicio");

  formulario.reset();

  document.getElementById("ejercicio-id").value = "";
  document.getElementById("ejercicio-nivel").value = nivelSeleccionadoId;

  document.getElementById("ejercicio-titulo").value = "";
  document.getElementById("ejercicio-consigna").value = "";
  document.getElementById("ejercicio-explicacion").value = "";
  document.getElementById("ejercicio-ejemplo").value = "";
  document.getElementById("ejercicio-resultado").value = "";
  document.getElementById("ejercicio-codigo").value = "";
  document.getElementById("ejercicio-orden").value = "";
  document.getElementById("ejercicio-nivel").value = "";
  document.getElementById("ejercicio-bloques").value = "";
  document.getElementById("ejercicio-pistas").value = "";

  document.getElementById("ejercicio-modal-title").textContent = id
    ? "Modificar ejercicio"
    : "Crear ejercicio";

  if (id) {
    try {
      const ejercicio = await adminFetch(`/admin/ejercicios/${id}`);

      document.getElementById("ejercicio-id").value = ejercicio.id;
      document.getElementById("ejercicio-titulo").value =
        ejercicio.titulo || "";
      document.getElementById("ejercicio-consigna").value =
        ejercicio.consigna || "";
      document.getElementById("ejercicio-explicacion").value =
        ejercicio.explicacion || "";
      document.getElementById("ejercicio-ejemplo").value =
        ejercicio.ejemploDiferente || "";
      document.getElementById("ejercicio-resultado").value =
        ejercicio.resultadoEjemplo || "";
      document.getElementById("ejercicio-codigo").value =
        ejercicio.codigoEsperado || "";
      document.getElementById("ejercicio-orden").value =
        ejercicio.ordenPresentacion || 1;
      document.getElementById("ejercicio-nivel").value = ejercicio.nivelId;

      document.getElementById("ejercicio-bloques").value = JSON.stringify(
        ejercicio.bloques || [],
        null,
        2,
      );

      document.getElementById("ejercicio-pistas").value = JSON.stringify(
        ejercicio.pistas || [],
        null,
        2,
      );
    } catch (error) {
      alert("No se pudo cargar el ejercicio: " + error.message);
      return;
    }
  }

  document.getElementById("modal-ejercicio").classList.add("show");
}

// CERRAR MODAL DE EJERCICIO

function cerrarModalEjercicio() {
  document.getElementById("modal-ejercicio").classList.remove("show");
}

// GUARDAR MODIFICACIÓN DEL EJERCICIO

async function guardarEjercicio() {
  const id = document.getElementById("ejercicio-id").value;

  let bloques;
  let pistas;

  try {
    bloques = JSON.parse(document.getElementById("ejercicio-bloques").value);

    pistas = JSON.parse(document.getElementById("ejercicio-pistas").value);

    if (!Array.isArray(bloques) || !Array.isArray(pistas)) {
      throw new Error("Los bloques y las pistas deben ser listas JSON.");
    }
  } catch (error) {
    alert("Revisá el formato de bloques y pistas: " + error.message);
    return;
  }

  const payload = {
    titulo: document.getElementById("ejercicio-titulo").value,
    consigna: document.getElementById("ejercicio-consigna").value,
    explicacion: document.getElementById("ejercicio-explicacion").value,
    ejemploDiferente: document.getElementById("ejercicio-ejemplo").value,
    resultadoEjemplo: document.getElementById("ejercicio-resultado").value,
    codigoEsperado: document.getElementById("ejercicio-codigo").value,
    ordenPresentacion: Number(document.getElementById("ejercicio-orden").value),
    nivelId: Number(document.getElementById("ejercicio-nivel").value),
    bloques: bloques,
    pistas: pistas,
  };

  try {
    if (id) {
      await adminFetch(`/admin/ejercicios/${id}`, {
        method: "PUT",
        body: JSON.stringify(payload),
      });
    } else {
      await adminFetch("/admin/ejercicios", {
        method: "POST",
        body: JSON.stringify(payload),
      });
    }

    cerrarModalEjercicio();

    await cargarEjerciciosAdmin(payload.nivelId, tituloNivelSeleccionado);

    alert(
      id
        ? "Ejercicio modificado correctamente."
        : "Ejercicio creado correctamente.",
    );
  } catch (error) {
    alert("No se pudo guardar el ejercicio: " + error.message);
  }
}

async function eliminarEjercicio(id) {
  const confirmar = window.confirm(
    "¿Estás seguro de que querés eliminar este ejercicio? Esta acción no se puede deshacer.",
  );

  if (!confirmar) {
    return;
  }

  try {
    await adminFetch(`/admin/ejercicios/${id}`, {
      method: "DELETE",
    });

    await cargarEjerciciosAdmin(nivelSeleccionadoId, tituloNivelSeleccionado);

    alert("Ejercicio eliminado correctamente.");
  } catch (error) {
    alert("No se pudo eliminar el ejercicio: " + error.message);
  }
}
