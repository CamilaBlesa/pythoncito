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
        "Content-Type": "application/json",
      },
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
  const token = await obtenerTokenEstudiante();

  const headers = {
    "Content-Type": "application/json",
    ...(opciones.headers || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const respuesta = await fetch(`${API_BASE}${endpoint}`, {
    ...opciones,
    headers,
  });

  if (respuesta.status === 401) {
    throw new Error("Tu sesión venció. Necesitás recuperar tu acceso.");
  }

  if (respuesta.status === 403) {
    throw new Error("No tenés permiso para realizar esta operación.");
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

function inicializarPreferenciasAccesibilidad() {
    const altoContraste =
        localStorage.getItem("a11y_high_contrast") === "true";

    const fuenteSeleccionada =
        localStorage.getItem("fuenteSeleccionada") || "default";

    const tamanoFuente =
        localStorage.getItem("a11y_font_size") || "base";

    // Alto contraste
    if (altoContraste) {
        document.body.classList.add("high-contrast");
    }

    // Aplicar fuente guardada
    document.body.classList.remove(
        "font-atkinson",
        "font-opendyslexic",
        "font-dyslexic"
    );

    if (fuenteSeleccionada === "atkinson") {
        document.body.classList.add("font-atkinson");
    } else if (fuenteSeleccionada === "opendyslexic") {
        document.body.classList.add("font-opendyslexic");
    }

    // Tamaño de fuente
    document.documentElement.classList.remove("font-lg", "font-xl");
    document.body.classList.remove("font-lg", "font-xl");

    if (tamanoFuente === "lg") {
        document.documentElement.classList.add("font-lg");
        document.body.classList.add("font-lg");
    } else if (tamanoFuente === "xl") {
        document.documentElement.classList.add("font-xl");
        document.body.classList.add("font-xl");
    }

    // Botón de contraste
    const btnContraste = document.getElementById("toggle-contrast");

    if (btnContraste) {
        btnContraste.classList.toggle("active", altoContraste);
        btnContraste.setAttribute("aria-pressed", String(altoContraste));

        btnContraste.addEventListener("click", () => {
            const activo = document.body.classList.toggle("high-contrast");

            localStorage.setItem("a11y_high_contrast", activo);
            btnContraste.classList.toggle("active", activo);
            btnContraste.setAttribute("aria-pressed", String(activo));
        });
    }

    // Botones de selección de fuente
    const botonesFuente = document.querySelectorAll("[data-font]");

    botonesFuente.forEach((boton) => {
        const fuente = boton.dataset.font;

        boton.classList.toggle("active", fuente === fuenteSeleccionada);
        boton.setAttribute(
            "aria-pressed",
            String(fuente === fuenteSeleccionada)
        );

        boton.addEventListener("click", () => {
            const nuevaFuente = boton.dataset.font;

            document.body.classList.remove(
                "font-atkinson",
                "font-opendyslexic",
                "font-dyslexic"
            );

            if (nuevaFuente === "atkinson") {
                document.body.classList.add("font-atkinson");
            } else if (nuevaFuente === "opendyslexic") {
                document.body.classList.add("font-opendyslexic");
            }

            localStorage.setItem("fuenteSeleccionada", nuevaFuente);

            botonesFuente.forEach((otroBoton) => {
                const seleccionado =
                    otroBoton.dataset.font === nuevaFuente;

                otroBoton.classList.toggle("active", seleccionado);
                otroBoton.setAttribute(
                    "aria-pressed",
                    String(seleccionado)
                );
            });
        });
    });

    // Botón de tamaño de fuente
    const btnTamano = document.getElementById("toggle-fontsize");

    if (btnTamano) {
        actualizarTextoBotonTamano(btnTamano, tamanoFuente);

        btnTamano.addEventListener("click", () => {
            const actual =
                localStorage.getItem("a11y_font_size") || "base";

            let siguiente = "base";

            if (actual === "base") {
                siguiente = "lg";
            } else if (actual === "lg") {
                siguiente = "xl";
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
        boton.textContent = "🔍 Texto: Mediano";
    } else if (tamano === "xl") {
        boton.textContent = "🔍 Texto: Grande";
    } else {
        boton.textContent = "🔍 Texto: Predeterminado";
    }
}
