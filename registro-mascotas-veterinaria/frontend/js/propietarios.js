const API_URL = "http://localhost:8080/api/propietarios";

const formulario = document.getElementById("formPropietario");
const mensaje = document.getElementById("mensajePropietario");

formulario.addEventListener("submit", async function (evento) {
    evento.preventDefault();

    const propietario = {
        nombre: document.getElementById("nombre").value.trim(),
        apellido: document.getElementById("apellido").value.trim(),
        telefono: document.getElementById("telefono").value.trim(),
        correo: document.getElementById("correo").value.trim(),
        direccion: document.getElementById("direccion").value.trim()
    };

    mensaje.textContent = "Guardando propietario...";

    try {
        const respuesta = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(propietario)
        });

        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.error || "No se pudo guardar el propietario."
            );
        }

        mensaje.textContent = "Propietario guardado correctamente.";
        formulario.reset();

    } catch (error) {
        console.error("Error:", error);

        mensaje.textContent =
            "Error al guardar. Comprueba que el servidor Java esté iniciado. "
            + error.message;
    }
});