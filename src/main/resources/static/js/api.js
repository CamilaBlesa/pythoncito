const API_BASE = "/api";

async function obtenerTokenEstudiante() {
    let token = localStorage.getItem("pythoncito_token");
    if (token) {
        return token;
    }

    try {
        const respuesta = await fetch(`${API_BASE}/auth/estudiante`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!respuesta.ok) {
            throw new Error("No se pudo iniciar la sesión de estudiante");
        }

        const data = await respuesta.json();
        token = data.token;
        localStorage.setItem("pythoncito_token", token);
        return token;
    } catch (error) {
        console.error("Error al autenticar estudiante:", error);
        throw error;
    }
}

async function apiFetch(endpoint, opciones = {}) {
    let token = await obtenerTokenEstudiante();

    const headers = {
        "Content-Type": "application/json",
        ...(opciones.headers || {})
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    let respuesta = await fetch(`${API_BASE}${endpoint}`, {
        ...opciones,
        headers
    });

    if (respuesta.status === 401) {
        localStorage.removeItem("pythoncito_token");
        token = await obtenerTokenEstudiante();
        headers["Authorization"] = `Bearer ${token}`;
        respuesta = await fetch(`${API_BASE}${endpoint}`, {
            ...opciones,
            headers
        });
    }

    if (!respuesta.ok) {
        let detalleError = "Error en la petición";
        try {
            const errorJson = await respuesta.json();
            detalleError = errorJson.error || errorJson.mensaje || errorJson.message || JSON.stringify(errorJson);
        } catch (e) {
            detalleError = `Error HTTP ${respuesta.status}: ${respuesta.statusText}`;
        }
        throw new Error(detalleError);
    }

    return await respuesta.json();
}

function inicializarPreferenciasAccesibilidad() {
    const altoContraste = localStorage.getItem("a11y_high_contrast") === "true";
    const fuenteDislexia = localStorage.getItem("a11y_dyslexic") === "true";
    const tamanoFuente = localStorage.getItem("a11y_font_size") || "base";

    if (altoContraste) {
        document.body.classList.add("high-contrast");
    }
    if (fuenteDislexia) {
        document.body.classList.add("font-dyslexic");
    }
    if (tamanoFuente === "lg") {
        document.documentElement.classList.add("font-lg");
        document.body.classList.add("font-lg");
    } else if (tamanoFuente === "xl") {
        document.documentElement.classList.add("font-xl");
        document.body.classList.add("font-xl");
    }

    const btnContraste = document.getElementById("toggle-contrast");
    if (btnContraste) {
        btnContraste.classList.toggle("active", altoContraste);
        btnContraste.addEventListener("click", () => {
            const activo = document.body.classList.toggle("high-contrast");
            localStorage.setItem("a11y_high_contrast", activo);
            btnContraste.classList.toggle("active", activo);
        });
    }

    const btnDislexia = document.getElementById("toggle-dyslexic");
    if (btnDislexia) {
        btnDislexia.classList.toggle("active", fuenteDislexia);
        btnDislexia.addEventListener("click", () => {
            const activo = document.body.classList.toggle("font-dyslexic");
            localStorage.setItem("a11y_dyslexic", activo);
            btnDislexia.classList.toggle("active", activo);
        });
    }

    const btnTamano = document.getElementById("toggle-fontsize");
    if (btnTamano) {
        actualizarTextoBotonTamano(btnTamano, tamanoFuente);
        btnTamano.addEventListener("click", () => {
            let actual = localStorage.getItem("a11y_font_size") || "base";
            let siguiente = "base";
            if (actual === "base") {
                siguiente = "lg";
            } else if (actual === "lg") {
                siguiente = "xl";
            } else {
                siguiente = "base";
            }

            document.documentElement.classList.remove("font-lg", "font-xl");
            document.body.classList.remove("font-lg", "font-xl");
            if (siguiente === "lg") {
                document.documentElement.classList.add("font-lg");
                document.body.classList.add("font-lg");
            } else if (siguiente === "xl") {
                document.documentElement.classList.add("font-xl");
                document.body.classList.add("font-xl");
            }

            localStorage.setItem("a11y_font_size", siguiente);
            actualizarTextoBotonTamano(btnTamano, siguiente);
        });
    }
}

function actualizarTextoBotonTamano(boton, tamano) {
    if (tamano === "lg") {
        boton.innerHTML = "🔍 Texto: Grande";
    } else if (tamano === "xl") {
        boton.innerHTML = "🔍 Texto: Muy Grande";
    } else {
        boton.innerHTML = "🔍 Texto: Normal";
    }
}
