document.addEventListener("DOMContentLoaded", async () => {
    inicializarPreferenciasAccesibilidad();
    await cargarNiveles();
});

async function cargarNiveles() {
    const contenedor = document.getElementById("levels-container");
    const statsCompleted = document.getElementById("stats-completed");
    const progressFill = document.getElementById("progress-bar-fill");
    const progressBar = document.getElementById("progress-bar-container");

    try {
        const niveles = await apiFetch("/estudiante/niveles");

        contenedor.innerHTML = "";

        if (!niveles || niveles.length === 0) {
            contenedor.innerHTML = `<div class="empty-placeholder">No se encontraron niveles disponibles.</div>`;
            return;
        }

        let completadosCount = 0;

        for (let i = 0; i < niveles.length; i++) {
            const nivel = niveles[i];
            if (nivel.completado) {
                completadosCount++;
            }

            const card = document.createElement("article");
            card.className = "level-card";
            card.setAttribute("role", "listitem");

            let badgeHtml = "";
            let btnHtml = "";

            if (nivel.completado) {
                card.classList.add("completed", "unlocked");
                badgeHtml = `<span class="level-badge badge-completed">⭐ Completado</span>`;
                btnHtml = `<a href="/nivel.html?id=${nivel.id}" class="btn-primary" aria-label="Repasar ${nivel.titulo}">Repasar Nivel ➡️</a>`;
            } else if (nivel.desbloqueado) {
                card.classList.add("unlocked");
                badgeHtml = `<span class="level-badge badge-unlocked">🔓 Desbloqueado</span>`;
                btnHtml = `<a href="/nivel.html?id=${nivel.id}" class="btn-primary" aria-label="Comenzar ${nivel.titulo}">Jugar Nivel 🚀</a>`;
            } else {
                card.classList.add("locked");
                badgeHtml = `<span class="level-badge badge-locked">🔒 Bloqueado</span>`;
                btnHtml = `<button class="btn-disabled" disabled aria-disabled="true">Completa el anterior</button>`;
            }

            card.innerHTML = `
                <div>
                    ${badgeHtml}
                    <h3 class="level-title">${nivel.titulo}</h3>
                    <p class="level-theme">${nivel.tema || "Aprende los conceptos fundamentales de este nivel."}</p>
                </div>
                ${btnHtml}
            `;

            contenedor.appendChild(card);
        }

        const porcentaje = Math.round((completadosCount / niveles.length) * 100);
        statsCompleted.textContent = `${completadosCount} / ${niveles.length}`;
        progressFill.style.width = `${porcentaje}%`;
        progressBar.setAttribute("aria-valuenow", porcentaje);

    } catch (error) {
        const titulo = document.createElement("h2");
        titulo.textContent = nivel.titulo || "Error al cargar los niveles";
        titulo.className = "level-title";
        contenedor.appendChild(titulo);
    }
}
