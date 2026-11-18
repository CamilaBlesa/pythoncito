let adminToken = localStorage.getItem("pythoncito_admin_token");

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
            localStorage.removeItem("pythoncito_admin_token");
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
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ clave })
        });

        if (!respuesta.ok) {
            throw new Error("Clave docente incorrecta.");
        }

        const data = await respuesta.json();
        adminToken = data.token;
        localStorage.setItem("pythoncito_admin_token", adminToken);
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
        "Authorization": `Bearer ${adminToken}`,
        ...(opciones.headers || {})
    };

    const respuesta = await fetch(`/api${endpoint}`, {
        ...opciones,
        headers
    });

    if (respuesta.status === 401 || respuesta.status === 403) {
        localStorage.removeItem("pythoncito_admin_token");
        adminToken = null;
        mostrarLogin();
        throw new Error("Sesión expirada o no autorizada");
    }

    if (!respuesta.ok) {
        const err = await respuesta.json().catch(() => ({}));
        throw new Error(err.mensaje || err.error || "Error en la petición de administración");
    }

    return await respuesta.json();
}

async function cargarNivelesAdmin() {
    const tbody = document.getElementById("niveles-tbody");

    try {
        const niveles = await adminFetch("/admin/niveles");
        tbody.innerHTML = "";

        if (!niveles || niveles.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center;">No hay niveles registrados.</td></tr>`;
            return;
        }

        for (let i = 0; i < niveles.length; i++) {
            const n = niveles[i];
            const tr = document.createElement("tr");

            const estadoHtml = n.activo
                ? `<span style="color: var(--success); font-weight: 700;">Activo</span>`
                : `<span style="color: var(--danger); font-weight: 700;">Inactivo</span>`;

            tr.innerHTML = `
                <td>${n.id}</td>
                <td>${n.ordenPresentacion || "-"}</td>
                <td><strong>${n.titulo}</strong></td>
                <td>${n.tema || "-"}</td>
                <td>${estadoHtml}</td>
                <td style="display: flex; gap: 0.5rem;">
                    <button type="button" class="btn-secondary" style="padding: 0.35rem 0.65rem; font-size: 0.85rem;" onclick="editarNivel(${n.id}, '${n.titulo.replace(/'/g, "\\'")}', '${(n.tema || "").replace(/'/g, "\\'")}', ${n.ordenPresentacion}, ${n.activo})">
                        ✏️ Modificar
                    </button>
                    ${n.activo ? `
                    <button type="button" class="btn-secondary" style="padding: 0.35rem 0.65rem; font-size: 0.85rem; color: var(--danger);" onclick="darDeBajaNivel(${n.id})">
                        🗑️ Dar de baja
                    </button>` : ""}
                </td>
            `;

            tbody.appendChild(tr);
        }

    } catch (error) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--danger);">${error.message}</td></tr>`;
    }
}

function abrirModalNivel(id = null, titulo = "", tema = "", orden = 1, activo = true) {
    document.getElementById("nivel-id").value = id || "";
    document.getElementById("nivel-titulo").value = titulo;
    document.getElementById("nivel-tema").value = tema;
    document.getElementById("nivel-orden").value = orden;
    document.getElementById("nivel-activo").checked = activo;

    document.getElementById("modal-title").textContent = id ? "Modificar Nivel" : "Crear Nivel";
    document.getElementById("modal-nivel").classList.add("show");
}

function cerrarModalNivel() {
    document.getElementById("modal-nivel").classList.remove("show");
}

window.editarNivel = function(id, titulo, tema, orden, activo) {
    abrirModalNivel(id, titulo, tema, orden, activo);
};

window.darDeBajaNivel = async function(id) {
    if (!confirm("¿Estás seguro de que deseas dar de baja este nivel?")) {
        return;
    }

    try {
        await adminFetch(`/admin/niveles/${id}/baja`, {
            method: "PATCH",
            body: JSON.stringify({ activo: false })
        });
        await cargarNivelesAdmin();
    } catch (error) {
        alert("Error al dar de baja: " + error.message);
    }
};

async function guardarNivel() {
    const id = document.getElementById("nivel-id").value;
    const titulo = document.getElementById("nivel-titulo").value;
    const tema = document.getElementById("nivel-tema").value;
    const ordenPresentacion = parseInt(document.getElementById("nivel-orden").value, 10);
    const activo = document.getElementById("nivel-activo").checked;

    const payload = {
        titulo,
        tema,
        ordenPresentacion,
        activo
    };

    try {
        if (id) {
            await adminFetch(`/admin/niveles/${id}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });
        } else {
            await adminFetch("/admin/niveles", {
                method: "POST",
                body: JSON.stringify(payload)
            });
        }

        cerrarModalNivel();
        await cargarNivelesAdmin();

    } catch (error) {
        alert("Error al guardar nivel: " + error.message);
    }
}
