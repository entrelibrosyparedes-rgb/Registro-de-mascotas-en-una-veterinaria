const API_VACUNAS = "http://localhost:8080/api/vacunas";
const API_MASCOTAS = "http://localhost:8080/api/mascotas";

const formulario = document.getElementById("formVacuna");
const mensaje = document.getElementById("mensajeVacuna");
const selectorMascota = document.getElementById("mascotaId");

async function cargarMascotas() {
    try {
        const respuesta = await fetch(API_MASCOTAS);

        if (!respuesta.ok) {
            throw new Error("No se pudieron consultar las mascotas.");
        }

        const mascotas = await respuesta.json();

        selectorMascota.innerHTML =
            '<option value="">Selecciona una mascota</option>';

        mascotas.forEach(function (mascota) {
            const opcion = document.createElement("option");

            opcion.value = mascota.id;
            opcion.textContent =
                `${mascota.nombre} - ${mascota.especie}`;

            selectorMascota.appendChild(opcion);
        });

        if (mascotas.length === 0) {
            mensaje.textContent =
                "Primero debes registrar una mascota.";
        }
    } catch (error) {
        console.error("Error al cargar mascotas:", error);

        mensaje.textContent =
            "No se pudieron cargar las mascotas. Comprueba que el servidor Java esté iniciado.";
    }
}

formulario.addEventListener("submit", async function (evento) {
    evento.preventDefault();

    const vacuna = {
        mascotaId: selectorMascota.value,
        nombre: document.getElementById("nombreVacuna").value.trim(),
        fechaAplicacion:
            document.getElementById("fechaAplicacion").value,
        proximaFecha:
            document.getElementById("proximaFecha").value,
        observaciones:
            document.getElementById("observaciones").value.trim()
    };

    if (!vacuna.mascotaId) {
        mensaje.textContent = "Debes seleccionar una mascota.";
        return;
    }

    if (!vacuna.nombre || !vacuna.fechaAplicacion) {
        mensaje.textContent =
            "El nombre y la fecha de aplicación son obligatorios.";
        return;
    }

    mensaje.textContent = "Guardando vacuna...";

    try {
        const respuesta = await fetch(API_VACUNAS, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(vacuna)
        });

        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.error || "No se pudo guardar la vacuna."
            );
        }

        mensaje.textContent =
            "¡Vacuna guardada correctamente y relacionada con la mascota!";

        formulario.reset();

    } catch (error) {
        console.error("Error al guardar la vacuna:", error);

        mensaje.textContent =
            "Error al guardar la vacuna: " + error.message;
    }
});

cargarMascotas();