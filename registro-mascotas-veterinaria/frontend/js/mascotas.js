document.getElementById("formMascota").addEventListener("submit", async function (e) {
    e.preventDefault();

    const datos = {
        nombre: this.nombre.value,
        especie: this.especie.value,
        raza: this.raza.value,
        sexo: this.sexo.value,
        fechaNacimiento: this.fechaNacimiento.value,
        peso: this.peso.value,
        propietario: this.propietario.value
    };

    try {
        const respuesta = await fetch("http://localhost:8080/api/mascotas", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            alert(resultado.error || "No se pudo registrar la mascota.");
            return;
        }

        alert(resultado.mensaje);
        this.reset();

    } catch (error) {
        alert("No se pudo conectar con el servidor Java.");
        console.error(error);
    }
});