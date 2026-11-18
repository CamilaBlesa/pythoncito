let nivelActual = null;
let ejercicioActual = null;
let bloquesDisponibles = [];
let bloquesSolucion = [];
let ordenPistaActual = 1;
let dragElementoOrigen = null;
let dragIndiceOrigen = null;
let dragZonaOrigen = null;

document.addEventListener("DOMContentLoaded", async () => {
    inicializarPreferenciasAccesibilidad();
    configurarEventosControles();
    await iniciarDesafio();
});

function configurarEventosControles() {
    const btnReset = document.getElementById("btn-reset");
    if (btnReset) {
        btnReset.addEventListener("click", reiniciarBloques);
    }

    const btnHint = document.getElementById("btn-hint");
    if (btnHint) {
        btnHint.addEventListener("click", pedirPista);
    }

    const btnValidate = document.getElementById("btn-validate");
    if (btnValidate) {
        btnValidate.addEventListener("click", validarSolucion);
    }

    const zonaBank = document.getElementById("zone-bank");
    const zonaSolution = document.getElementById("zone-solution");

    configurarZonaDrop(zonaBank, "bank");
    configurarZonaDrop(zonaSolution, "solution");
}

async function iniciarDesafio() {
    const urlParams = new URLSearchParams(window.location.search);
    const nivelId = urlParams.get("id");

    if (!nivelId) {
        window.location.href = "/index.html";
        return;
    }

    try {
        nivelActual = await apiFetch(`/estudiante/niveles/${nivelId}`);
        document.getElementById("header-level-title").textContent = nivelActual.titulo;

        const ejercicios = await apiFetch(`/estudiante/niveles/${nivelId}/ejercicios`);

        if (!ejercicios || ejercicios.length === 0) {
            alert("No hay ejercicios cargados para este nivel.");
            window.location.href = "/index.html";
            return;
        }

        ejercicioActual = ejercicios[0];
        mostrarDetalleEjercicio();
        cargarBloquesIniciales();

    } catch (error) {
        alert("Error al cargar el desafío: " + error.message);
        window.location.href = "/index.html";
    }
}

function mostrarDetalleEjercicio() {
    document.getElementById("exercise-consigna").textContent = ejercicioActual.consigna;
    document.getElementById("exercise-explicacion").textContent = ejercicioActual.explicacion;
    document.getElementById("exercise-ejemplo").textContent = ejercicioActual.ejemploDiferente;
    document.getElementById("exercise-resultado-ejemplo").textContent = ejercicioActual.resultadoEjemplo;
}

function cargarBloquesIniciales() {
    bloquesDisponibles = [];
    bloquesSolucion = [];

    if (ejercicioActual.bloques) {
        for (let i = 0; i < ejercicioActual.bloques.length; i++) {
            bloquesDisponibles.push({ ...ejercicioActual.bloques[i] });
        }
    }

    renderizarBloques();
}

function renderizarBloques() {
    const contenedorBank = document.getElementById("zone-bank");
    const contenedorSolution = document.getElementById("zone-solution");
    const countBank = document.getElementById("bank-count");
    const countSolution = document.getElementById("solution-count");

    contenedorBank.querySelectorAll(".code-block").forEach(el => el.remove());
    contenedorSolution.querySelectorAll(".code-block").forEach(el => el.remove());

    countBank.textContent = `${bloquesDisponibles.length} disponible${bloquesDisponibles.length === 1 ? "" : "s"}`;
    countSolution.textContent = `${bloquesSolucion.length} en solución`;

    const placeholderBank = document.getElementById("bank-empty");
    if (placeholderBank) {
        placeholderBank.style.display = bloquesDisponibles.length === 0 ? "block" : "none";
    }

    const placeholderSolution = document.getElementById("solution-empty");
    if (placeholderSolution) {
        placeholderSolution.style.display = bloquesSolucion.length === 0 ? "block" : "none";
    }

    for (let i = 0; i < bloquesDisponibles.length; i++) {
        const bloque = bloquesDisponibles[i];
        const el = crearElementoBloque(bloque, "bank", i);
        contenedorBank.appendChild(el);
    }

    for (let i = 0; i < bloquesSolucion.length; i++) {
        const bloque = bloquesSolucion[i];
        const el = crearElementoBloque(bloque, "solution", i);
        contenedorSolution.appendChild(el);
    }
}

function crearElementoBloque(bloque, zona, indice) {
    const el = document.createElement("div");
    el.className = "code-block";
    el.setAttribute("draggable", "true");
    el.setAttribute("tabindex", "0");
    el.setAttribute("role", "listitem");
    el.dataset.id = bloque.id;
    el.dataset.zona = zona;
    el.dataset.indice = indice;

    const spanTexto = document.createElement("span");
    spanTexto.className = "block-content";
    spanTexto.textContent = bloque.contenido;
    el.appendChild(spanTexto);

    const acciones = document.createElement("div");
    acciones.className = "block-actions";

    if (zona === "bank") {
        const btnMover = document.createElement("button");
        btnMover.type = "button";
        btnMover.className = "btn-block-action";
        btnMover.setAttribute("aria-label", `Agregar ${bloque.contenido} a la solución`);
        btnMover.textContent = "➕ Usar";
        btnMover.addEventListener("click", (e) => {
            e.stopPropagation();
            moverASolucion(indice);
        });
        acciones.appendChild(btnMover);

        el.addEventListener("click", () => moverASolucion(indice));
        el.addEventListener("keydown", (e) => {
            if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                moverASolucion(indice);
            }
        });

    } else {
        const btnSubir = document.createElement("button");
        btnSubir.type = "button";
        btnSubir.className = "btn-block-action";
        btnSubir.setAttribute("aria-label", "Subir bloque");
        btnSubir.textContent = "▲";
        if (indice === 0) {
            btnSubir.disabled = true;
            btnSubir.style.opacity = "0.3";
        } else {
            btnSubir.addEventListener("click", (e) => {
                e.stopPropagation();
                subirBloque(indice);
            });
        }
        acciones.appendChild(btnSubir);

        const btnBajar = document.createElement("button");
        btnBajar.type = "button";
        btnBajar.className = "btn-block-action";
        btnBajar.setAttribute("aria-label", "Bajar bloque");
        btnBajar.textContent = "▼";
        if (indice === bloquesSolucion.length - 1) {
            btnBajar.disabled = true;
            btnBajar.style.opacity = "0.3";
        } else {
            btnBajar.addEventListener("click", (e) => {
                e.stopPropagation();
                bajarBloque(indice);
            });
        }
        acciones.appendChild(btnBajar);

        const btnQuitar = document.createElement("button");
        btnQuitar.type = "button";
        btnQuitar.className = "btn-block-action";
        btnQuitar.setAttribute("aria-label", `Quitar ${bloque.contenido} de la solución`);
        btnQuitar.textContent = "✖";
        btnQuitar.addEventListener("click", (e) => {
            e.stopPropagation();
            devolverABanco(indice);
        });
        acciones.appendChild(btnQuitar);

        el.addEventListener("keydown", (e) => {
            if (e.key === "ArrowUp" && indice > 0) {
                e.preventDefault();
                subirBloque(indice);
            } else if (e.key === "ArrowDown" && indice < bloquesSolucion.length - 1) {
                e.preventDefault();
                bajarBloque(indice);
            } else if (e.key === "Delete" || e.key === "Backspace") {
                e.preventDefault();
                devolverABanco(indice);
            }
        });
    }

    el.appendChild(acciones);

    el.addEventListener("dragstart", (e) => {
        dragElementoOrigen = el;
        dragZonaOrigen = zona;
        dragIndiceOrigen = indice;
        el.classList.add("dragging");
        e.dataTransfer.setData("text/plain", bloque.id);
        e.dataTransfer.effectAllowed = "move";
    });

    el.addEventListener("dragend", () => {
        el.classList.remove("dragging");
        document.querySelectorAll(".block-dropzone").forEach(z => z.classList.remove("drag-over"));
    });

    return el;
}

function moverASolucion(indice) {
    const bloque = bloquesDisponibles.splice(indice, 1)[0];
    bloquesSolucion.push(bloque);
    renderizarBloques();
    ocultarFeedback();
}

function devolverABanco(indice) {
    const bloque = bloquesSolucion.splice(indice, 1)[0];
    bloquesDisponibles.push(bloque);
    renderizarBloques();
    ocultarFeedback();
}

function subirBloque(indice) {
    if (indice > 0) {
        const temp = bloquesSolucion[indice];
        bloquesSolucion[indice] = bloquesSolucion[indice - 1];
        bloquesSolucion[indice - 1] = temp;
        renderizarBloques();
        ocultarFeedback();
    }
}

function bajarBloque(indice) {
    if (indice < bloquesSolucion.length - 1) {
        const temp = bloquesSolucion[indice];
        bloquesSolucion[indice] = bloquesSolucion[indice + 1];
        bloquesSolucion[indice + 1] = temp;
        renderizarBloques();
        ocultarFeedback();
    }
}

function reiniciarBloques() {
    cargarBloquesIniciales();
    ocultarFeedback();
}

function configurarZonaDrop(zonaElemento, nombreZona) {
    zonaElemento.addEventListener("dragover", (e) => {
        e.preventDefault();
        e.dataTransfer.dropEffect = "move";
        zonaElemento.classList.add("drag-over");
    });

    zonaElemento.addEventListener("dragleave", (e) => {
        if (!zonaElemento.contains(e.relatedTarget)) {
            zonaElemento.classList.remove("drag-over");
        }
    });

    zonaElemento.addEventListener("drop", (e) => {
        e.preventDefault();
        zonaElemento.classList.remove("drag-over");

        if (dragZonaOrigen === nombreZona && nombreZona === "solution") {
            const dropTarget = e.target.closest(".code-block");
            if (dropTarget && dropTarget !== dragElementoOrigen) {
                const destinoIndice = parseInt(dropTarget.dataset.indice, 10);
                const item = bloquesSolucion.splice(dragIndiceOrigen, 1)[0];
                bloquesSolucion.splice(destinoIndice, 0, item);
                renderizarBloques();
                ocultarFeedback();
            }
            return;
        }

        if (dragZonaOrigen === "bank" && nombreZona === "solution") {
            const item = bloquesDisponibles.splice(dragIndiceOrigen, 1)[0];
            const dropTarget = e.target.closest(".code-block");
            if (dropTarget) {
                const destinoIndice = parseInt(dropTarget.dataset.indice, 10);
                bloquesSolucion.splice(destinoIndice, 0, item);
            } else {
                bloquesSolucion.push(item);
            }
            renderizarBloques();
            ocultarFeedback();
            return;
        }

        if (dragZonaOrigen === "solution" && nombreZona === "bank") {
            const item = bloquesSolucion.splice(dragIndiceOrigen, 1)[0];
            bloquesDisponibles.push(item);
            renderizarBloques();
            ocultarFeedback();
            return;
        }
    });
}

async function pedirPista() {
    try {
        const pista = await apiFetch(`/estudiante/ejercicios/${ejercicioActual.id}/pistas/${ordenPistaActual}`);
        mostrarPista(pista);
        ordenPistaActual++;
    } catch (error) {
        alert(error.message || "No hay más pistas disponibles para este ejercicio.");
    }
}

function mostrarPista(pista) {
    const wrapper = document.getElementById("hints-wrapper");
    const lista = document.getElementById("hints-list");
    wrapper.style.display = "block";

    const card = document.createElement("div");
    card.className = "hint-card";
    card.innerHTML = `
        <span style="font-size: 1.5rem;" aria-hidden="true">💡</span>
        <div>
            <strong>Pista ${pista.orden}:</strong>
            <p>${pista.texto}</p>
        </div>
    `;
    lista.appendChild(card);
}

async function validarSolucion() {
    if (bloquesSolucion.length === 0) {
        alert("Debes colocar al menos un bloque en tu solución antes de comprobar.");
        return;
    }

    const idsSolucion = [];
    for (let i = 0; i < bloquesSolucion.length; i++) {
        idsSolucion.push(bloquesSolucion[i].id);
    }

    try {
        const resultado = await apiFetch(`/estudiante/ejercicios/${ejercicioActual.id}/validar`, {
            method: "POST",
            body: JSON.stringify(idsSolucion)
        });

        mostrarFeedback(resultado);

    } catch (error) {
        alert("Error al validar el ejercicio: " + error.message);
    }
}

function mostrarFeedback(resultado) {
    const panel = document.getElementById("feedback-panel");
    const icono = document.getElementById("feedback-icon");
    const titulo = document.getElementById("feedback-title");
    const mensaje = document.getElementById("feedback-message");
    const detalles = document.getElementById("feedback-details");
    const nextContainer = document.getElementById("feedback-next-container");

    panel.classList.remove("feedback-success", "feedback-error");
    detalles.innerHTML = "";

    if (resultado.correcta) {
        panel.classList.add("feedback-success", "show");
        icono.textContent = "🎉";
        titulo.textContent = "¡Felicitaciones! Solución Correcta";
        mensaje.textContent = resultado.mensaje || "Has ordenado el código a la perfección.";
        nextContainer.style.display = "block";

        document.querySelectorAll("#zone-solution .code-block").forEach(b => {
            b.style.borderColor = "var(--success)";
        });

    } else {
        panel.classList.add("feedback-error", "show");
        icono.textContent = "🤔";
        titulo.textContent = "Casi lo tienes, revisemos algunos detalles:";
        mensaje.textContent = resultado.mensaje || "El código no produjo el resultado esperado.";
        nextContainer.style.display = "none";

        if (resultado.bloquesFaltantes && resultado.bloquesFaltantes.length > 0) {
            const item = document.createElement("div");
            item.className = "feedback-item";
            item.innerHTML = `⚠️ <strong>Bloques faltantes:</strong> A tu solución le falta${resultado.bloquesFaltantes.length === 1 ? " 1 bloque necesario" : `n ${resultado.bloquesFaltantes.length} bloques necesarios`}. Revisa el banco de bloques.`;
            detalles.appendChild(item);
        }

        if (resultado.bloquesSobrantes && resultado.bloquesSobrantes.length > 0) {
            const item = document.createElement("div");
            item.className = "feedback-item";
            item.innerHTML = `🚫 <strong>Bloques sobrantes:</strong> Has colocado bloques que no pertenecen a este ejercicio. Quítalos de tu solución.`;
            detalles.appendChild(item);
        }

        if (resultado.bloquesFueraDeOrden && resultado.bloquesFueraDeOrden.length > 0) {
            const item = document.createElement("div");
            item.className = "feedback-item";
            item.innerHTML = `🔀 <strong>Bloques fuera de orden:</strong> Hay bloques colocados antes o después de lo debido. Prueba usar los botones ▲ y ▼ para corregir su posición.`;
            detalles.appendChild(item);

            document.querySelectorAll("#zone-solution .code-block").forEach(el => {
                const id = parseInt(el.dataset.id, 10);
                if (resultado.bloquesFueraDeOrden.includes(id)) {
                    el.style.borderColor = "var(--danger)";
                } else {
                    el.style.borderColor = "transparent";
                }
            });
        }
    }

    panel.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

function ocultarFeedback() {
    const panel = document.getElementById("feedback-panel");
    panel.classList.remove("show");
    document.querySelectorAll("#zone-solution .code-block").forEach(b => {
        b.style.borderColor = "transparent";
    });
}
