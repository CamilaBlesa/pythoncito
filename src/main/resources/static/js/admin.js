let adminToken = sessionStorage.getItem("pythoncito_admin_token");

document.addEventListener("DOMContentLoaded", () => {
  inicializarPreferenciasAccesibilidad();
  configurarEventosAdmin();

  if (adminToken) {
    mostrarDashboard();
  } else {
    mostrarLogin();
  }
});

function configurarEventosAdmin() {
  const loginForm = document.getElementById("admin-login-form");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const clave = document.getElementById("admin-clave").value;
      await autenticarAdmin(clave);
    });
  }

  const btnLogout = document.getElementById("btn-admin-logout");
  if (btnLogout) {
    btnLogout.addEventListener("click", () => {
      sessionStorage.removeItem("pythoncito_admin_token");
      adminToken = null;
      mostrarLogin();
    });
  }

  const btnNuevoNivel = document.getElementById("btn-nuevo-nivel");
  if (btnNuevoNivel) {
    btnNuevoNivel.addEventListener("click", () => abrirModalNivel());
  }

  const btnCancelarModal = document.getElementById("btn-cancelar-modal");
  if (btnCancelarModal) {
    btnCancelarModal.addEventListener("click", () => cerrarModalNivel());
  }

  const formNivel = document.getElementById("form-nivel");
  if (formNivel) {
    formNivel.addEventListener("submit", async (e) => {
      e.preventDefault();
      await guardarNivel();
    });
  }
}

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

function mostrarLogin() {
  document.getElementById("login-section").style.display = "block";
  document.getElementById("dashboard-section").style.display = "none";
}

function mostrarDashboard() {
  document.getElementById("login-section").style.display = "none";
  document.getElementById("dashboard-section").style.display = "block";
  cargarNivelesAdmin();
}

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

  if (respuesta.status === 401 || respuesta.status === 403) {
    sessionStorage.removeItem("pythoncito_admin_token");
    adminToken = null;
    mostrarLogin();

    throw new Error("Tu sesión expiró o no tenés autorización.");
  }

  if (!respuesta.ok) {
    const error = await respuesta.json().catch(() => ({}));

    throw new Error(
      error.mensaje ||
        error.message ||
        error.error ||
        `Error HTTP ${respuesta.status}`,
    );
  }

  if (respuesta.status === 204) {
    return null;
  }

  const contenido = await respuesta.text();

  if (!contenido) {
    return null;
  }

  return JSON.parse(contenido);
}

async function cargarNivelesAdmin() {
    const tbody = document.getElementById("niveles-tbody");

    try {
        const niveles = await adminFetch("/admin/niveles");

        tbody.innerHTML = "";

        if (!niveles || niveles.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center;">
                        No hay niveles registrados.
                    </td>
                </tr>`;
            return;
        }

        for (let i = 0; i < niveles.length; i++) {
            const n = niveles[i];

            const tr = document.createElement("tr");

            // ID
            const tdId = document.createElement("td");
            tdId.textContent = n.id;
            tr.appendChild(tdId);

            // Orden
            const tdOrden = document.createElement("td");
            tdOrden.textContent = n.ordenPresentacion;
            tr.appendChild(tdOrden);

            // Título
            const tdTitulo = document.createElement("td");
            tdTitulo.textContent = n.titulo;
            tr.appendChild(tdTitulo);

            // Tema
            const tdTema = document.createElement("td");
            tdTema.textContent = n.tema;
            tr.appendChild(tdTema);

            // Estado
            const tdEstado = document.createElement("td");
            tdEstado.textContent = n.activo ? "Activo" : "Inactivo";
            tr.appendChild(tdEstado);

            // Acciones
            const tdAcciones = document.createElement("td");

            // Botón modificar
            const btnEditar = document.createElement("button");

            btnEditar.type = "button";
            btnEditar.className = "btn-secondary";
            btnEditar.textContent = "Modificar";

            btnEditar.addEventListener("click", () => {
                abrirModalNivel(
                    n.id,
                    n.titulo,
                    n.tema,
                    n.ordenPresentacion,
                    n.activo
                );
            });

            tdAcciones.appendChild(btnEditar);

            // Botón dar de baja
            if (n.activo) {
                const btnBaja = document.createElement("button");

                btnBaja.type = "button";
                btnBaja.className = "btn-secondary";
                btnBaja.textContent = "Dar de baja";

                btnBaja.addEventListener("click", () => {
                    darDeBajaNivel(n.id);
                });

                tdAcciones.appendChild(btnBaja);
            }

            tr.appendChild(tdAcciones);

            tbody.appendChild(tr);
        }

    } catch (error) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; color: var(--danger);">
                    ${error.message}
                </td>
            </tr>`;
    }
}

async function darDeBajaNivel(id) {
    const confirmar = window.confirm(
        "¿Estás seguro de que deseas dar de baja este nivel?"
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
        console.error(error);
    }
}

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

window.editarNivel = function (id, titulo, tema, orden, activo) {
  abrirModalNivel(id, titulo, tema, orden, activo);
};

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
    console.error(error);
  }
}

async function guardarNivel() {
  const id = document.getElementById("nivel-id").value;
  const titulo = document.getElementById("nivel-titulo").value;
  const tema = document.getElementById("nivel-tema").value;
  const ordenPresentacion = parseInt(
    document.getElementById("nivel-orden").value,
    10,
  );
  const activo = document.getElementById("nivel-activo").checked;

  const payload = {
    titulo,
    tema,
    ordenPresentacion,
    activo,
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
