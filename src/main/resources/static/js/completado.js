document.addEventListener("DOMContentLoaded", async () => {
    try {
        const niveles = await apiFetch("/estudiante/niveles");

        if (!niveles || niveles.length === 0) {
            window.location.href = "/index.html";
            return;
        }

        let cantidadCompletados = 0;

        for (let i = 0; i < niveles.length; i++) {
            if (niveles[i].completado) {
                cantidadCompletados++;
            }
        }

        const cursoCompleto =
            cantidadCompletados === niveles.length;

        if (!cursoCompleto) {
            window.location.href = "/index.html";
            return;
        }

        document.getElementById("titulo-final").textContent =
            "¡Terminaste Pythoncito!";

        document.getElementById("mensaje-final").textContent =
            "Completaste todos los niveles del curso.";

        document.getElementById("cantidad-niveles").textContent =
            `Completaste ${cantidadCompletados} de ${niveles.length} niveles.`;

    } catch (error) {
        window.location.href = "/index.html";
    }
});