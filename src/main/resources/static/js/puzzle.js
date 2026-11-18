let nivelActual = null;
let ejercicioActual = null;
let bloquesDisponibles = [];
let bloquesSolucion = [];
let ordenPistaActual = 1;
let dragElementoOrigen = null;
let dragIndiceOrigen = null;
let dragZonaOrigen = null;
let ejerciciosNivel = [];
let indiceEjercicioActual = 0;
let ejerciciosCompletados = [];

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

    document.getElementById("header-level-title").textContent =
      nivelActual.titulo;

    ejerciciosNivel = await apiFetch(
      `/estudiante/niveles/${nivelId}/ejercicios`,
    );

    if (!ejerciciosNivel || ejerciciosNivel.length === 0) {
      mostrarAvisoGeneral("No hay ejercicios cargados para este nivel.");
      return;
    }

    ejerciciosCompletados = [];

    for (let i = 0; i < ejerciciosNivel.length; i++) {
      const ejercicio = ejerciciosNivel[i];

      const intentos = await apiFetch(
        `/estudiante/ejercicios/${ejercicio.id}/intentos`,
      );

      const tieneIntentoCorrecto = intentos.some(
        (intento) => intento.correcta === true,
      );

      if (tieneIntentoCorrecto) {
        ejerciciosCompletados.push(ejercicio.id);
      }
    }

    indiceEjercicioActual = buscarPrimerPendiente();

    if (indiceEjercicioActual === -1) {
      indiceEjercicioActual = 0;
    }

    cargarEjercicioActual();
  } catch (error) {
    mostrarAvisoGeneral("Error al cargar el desafío: " + error.message);
  }
}

function buscarPrimerPendiente() {
  for (let i = 0; i < ejerciciosNivel.length; i++) {
    const idEjercicio = ejerciciosNivel[i].id;

    if (!ejerciciosCompletados.includes(idEjercicio)) {
      return i;
    }
  }

  return -1;
}

function cargarEjercicioActual() {
  ejercicioActual = ejerciciosNivel[indiceEjercicioActual];

  ordenPistaActual = 1;

  dragElementoOrigen = null;
  dragIndiceOrigen = null;
  dragZonaOrigen = null;

  document.getElementById("hints-list").textContent = "";
  document.getElementById("hints-wrapper").style.display = "none";

  document.getElementById("feedback-panel").classList.remove("show");

  document.getElementById("exercise-consigna").textContent =
    ejercicioActual.consigna || "";

  document.getElementById("exercise-explicacion").textContent =
    ejercicioActual.explicacion || "";

  document.getElementById("exercise-ejemplo").textContent =
    ejercicioActual.ejemploDiferente || "";

  document.getElementById("exercise-resultado-ejemplo").textContent =
    ejercicioActual.resultadoEjemplo || "";

  cargarBloquesIniciales();
}

function mostrarAvisoGeneral(mensaje) {
  const panel = document.getElementById("feedback-panel");

  panel.classList.add("show", "feedback-error");

  document.getElementById("feedback-title").textContent =
    "No se pudo cargar el ejercicio";

  document.getElementById("feedback-message").textContent = mensaje;
}

function mostrarDetalleEjercicio() {
  document.getElementById("exercise-consigna").textContent =
    ejercicioActual.consigna;
  document.getElementById("exercise-explicacion").textContent =
    ejercicioActual.explicacion;
  document.getElementById("exercise-ejemplo").textContent =
    ejercicioActual.ejemploDiferente;
  document.getElementById("exercise-resultado-ejemplo").textContent =
    ejercicioActual.resultadoEjemplo;
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

  contenedorBank.querySelectorAll(".code-block").forEach((el) => el.remove());
  contenedorSolution
    .querySelectorAll(".code-block")
    .forEach((el) => el.remove());

  countBank.textContent = `${bloquesDisponibles.length} disponible${bloquesDisponibles.length === 1 ? "" : "s"}`;
  countSolution.textContent = `${bloquesSolucion.length} en solución`;

  const placeholderBank = document.getElementById("bank-empty");
  if (placeholderBank) {
    placeholderBank.style.display =
      bloquesDisponibles.length === 0 ? "block" : "none";
  }

  const placeholderSolution = document.getElementById("solution-empty");
  if (placeholderSolution) {
    placeholderSolution.style.display =
      bloquesSolucion.length === 0 ? "block" : "none";
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
    btnMover.setAttribute(
      "aria-label",
      `Agregar ${bloque.contenido} a la solución`,
    );
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
    btnQuitar.setAttribute(
      "aria-label",
      `Quitar ${bloque.contenido} de la solución`,
    );
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
    document
      .querySelectorAll(".block-dropzone")
      .forEach((z) => z.classList.remove("drag-over"));
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
    const pista = await apiFetch(
      `/estudiante/ejercicios/${ejercicioActual.id}/pistas/${ordenPistaActual}`,
    );
    mostrarPista(pista);
    ordenPistaActual++;
  } catch (error) {
    mostrarAvisoPista(
      error.message || "No hay más pistas disponibles para este ejercicio.",
    );
  }
}

function mostrarPista(pista) {
  const wrapper = document.getElementById("hints-wrapper");
  const lista = document.getElementById("hints-list");

  wrapper.style.display = "block";

  const avisoExistente = document.getElementById("hint-notice-card");

  if (avisoExistente) {
    avisoExistente.remove();
  }

  const card = document.createElement("div");
  card.className = "hint-card";

  const icono = document.createElement("span");
  icono.setAttribute("aria-hidden", "true");
  icono.textContent = "💡";

  const contenido = document.createElement("div");

  const titulo = document.createElement("strong");
  titulo.textContent = `Pista ${pista.orden}:`;

  const texto = document.createElement("p");
  texto.textContent = pista.texto;

  contenido.appendChild(titulo);
  contenido.appendChild(texto);

  card.appendChild(icono);
  card.appendChild(contenido);

  lista.appendChild(card);

  card.scrollIntoView({
    behavior: "smooth",
    block: "nearest",
  });
}

function mostrarAvisoPista(mensaje) {
  const wrapper = document.getElementById("hints-wrapper");
  const lista = document.getElementById("hints-list");

  wrapper.style.display = "block";

  const avisoExistente = document.getElementById("hint-notice-card");

  if (avisoExistente) {
    avisoExistente.remove();
  }

  const card = document.createElement("div");
  card.id = "hint-notice-card";
  card.className = "hint-card";

  const icono = document.createElement("span");
  icono.textContent = "💡";
  icono.setAttribute("aria-hidden", "true");

  const contenido = document.createElement("div");

  const titulo = document.createElement("strong");
  titulo.textContent = "Pistas progresivas:";

  const texto = document.createElement("p");
  texto.textContent = mensaje;

  contenido.appendChild(titulo);
  contenido.appendChild(texto);

  card.appendChild(icono);
  card.appendChild(contenido);

  lista.appendChild(card);

  card.scrollIntoView({
    behavior: "smooth",
    block: "nearest",
  });
}

async function validarSolucion() {
  if (bloquesSolucion.length === 0) {
    alert(
      "Debes colocar al menos un bloque en tu solución antes de comprobar.",
    );
    return;
  }

  const idsSolucion = [];
  for (let i = 0; i < bloquesSolucion.length; i++) {
    idsSolucion.push(bloquesSolucion[i].id);
  }

  try {
    const resultado = await apiFetch(
      `/estudiante/ejercicios/${ejercicioActual.id}/validar`,
      {
        method: "POST",
        body: JSON.stringify(idsSolucion),
      },
    );

    await mostrarFeedback(resultado);
  } catch (error) {
    alert("Error al validar el ejercicio: " + error.message);
  }
}

async function mostrarFeedback(resultado) {
  const panel = document.getElementById("feedback-panel");
  const icono = document.getElementById("feedback-icon");
  const titulo = document.getElementById("feedback-title");
  const mensaje = document.getElementById("feedback-message");
  const detalles = document.getElementById("feedback-details");
  const nextContainer = document.getElementById("feedback-next-container");
  const btnNext = document.getElementById("btn-next-level");

  panel.classList.remove("feedback-success", "feedback-error");
  detalles.textContent = "";

  if (!resultado.correcta) {
    panel.classList.add("feedback-error", "show");

    icono.textContent = "🤔";
    titulo.textContent = "Todavía no es correcto";
    mensaje.textContent =
      resultado.mensaje || "Revisá el orden de los bloques.";

    nextContainer.style.display = "none";

    return;
  }

  panel.classList.add("feedback-success", "show");

  icono.textContent = "🎉";
  titulo.textContent = "¡Ejercicio correcto!";
  mensaje.textContent = "Resolviste correctamente este ejercicio.";

  if (!ejerciciosCompletados.includes(ejercicioActual.id)) {
    ejerciciosCompletados.push(ejercicioActual.id);
  }

  const siguiente = buscarSiguientePendiente();

  nextContainer.style.display = "block";

  if (siguiente !== -1) {
    btnNext.textContent = "Continuar al siguiente ejercicio";

    btnNext.href = "#";

    btnNext.onclick = (evento) => {
      evento.preventDefault();

      indiceEjercicioActual = siguiente;

      cargarEjercicioActual();

      panel.classList.remove("show");
    };

    return;
  }

  nivelActual = await apiFetch(`/estudiante/niveles/${nivelActual.id}`);

  if (nivelActual.completado) {
    titulo.textContent = "¡Nivel completado!";
    mensaje.textContent = "Terminaste todos los ejercicios de este nivel.";

    btnNext.textContent = "Volver al mapa";
    btnNext.href = "/index.html";
    btnNext.onclick = null;
  } else {
    titulo.textContent = "Ejercicios resueltos";
    mensaje.textContent =
      "El servidor todavía no confirmó la finalización del nivel.";

    nextContainer.style.display = "none";
  }
}
function ocultarFeedback() {
  const panel = document.getElementById("feedback-panel");
  panel.classList.remove("show");
  document.querySelectorAll("#zone-solution .code-block").forEach((b) => {
    b.style.borderColor = "transparent";
  });
}

function buscarSiguientePendiente() {
    for (
        let i = indiceEjercicioActual + 1;
        i < ejerciciosNivel.length;
        i++
    ) {
        if (!ejerciciosCompletados.includes(ejerciciosNivel[i].id)) {
            return i;
        }
    }

    return -1;
}