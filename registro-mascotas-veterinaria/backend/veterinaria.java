
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

abstract class veterinario {
    protected String nombreDueño;
    protected String nombreMascota;
    protected String especie;
    protected String vacunas;
    protected String fechasVacunas;
    protected String proximaVacuna;
    protected String sexo;
    protected String peso;

    public veterinario(
            String nombreDueño,
            String nombreMascota,
            String especie,
            String vacunas,
            String fechasVacunas,
            String proximaVacuna,
            String sexo,
            String peso) {

        this.nombreDueño = nombreDueño;
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.vacunas = vacunas;
        this.fechasVacunas = fechasVacunas;
        this.proximaVacuna = proximaVacuna;
        this.sexo = sexo;
        this.peso = peso;
    }

    public abstract void mostrarInformacion();
}

class Perro extends veterinario {
    private String raza;

    public Perro(
            String nombreDueño,
            String nombreMascota,
            String raza,
            String vacunas,
            String fechasVacunas,
            String proximaVacuna,
            String sexo,
            String peso) {

        super(nombreDueño, nombreMascota, "Perro", vacunas,
                fechasVacunas, proximaVacuna, sexo, peso);
        this.raza = raza;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Información del perro");
        System.out.println("Dueño: " + nombreDueño);
        System.out.println("Mascota: " + nombreMascota);
        System.out.println("Raza: " + raza);
        System.out.println("Sexo: " + sexo);
        System.out.println("Vacunas: " + vacunas);
        System.out.println("Fecha de vacunación: " + fechasVacunas);
        System.out.println("Próxima vacuna: " + proximaVacuna);
        System.out.println("Peso: " + peso);
    }
}

class Gato extends veterinario {
    private String raza;

    public Gato(
            String nombreDueño,
            String nombreMascota,
            String raza,
            String vacunas,
            String fechasVacunas,
            String proximaVacuna,
            String sexo,
            String peso) {

        super(nombreDueño, nombreMascota, "Gato", vacunas,
                fechasVacunas, proximaVacuna, sexo, peso);
        this.raza = raza;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Información del gato");
        System.out.println("Dueño: " + nombreDueño);
        System.out.println("Mascota: " + nombreMascota);
        System.out.println("Raza: " + raza);
        System.out.println("Sexo: " + sexo);
        System.out.println("Vacunas: " + vacunas);
        System.out.println("Fecha de vacunación: " + fechasVacunas);
        System.out.println("Próxima vacuna: " + proximaVacuna);
        System.out.println("Peso: " + peso);
    }
}

class Pajaros extends veterinario {
    private String especiePajaro;

    public Pajaros(
            String nombreDueño,
            String nombreMascota,
            String especiePajaro,
            String vacunas,
            String fechasVacunas,
            String proximaVacuna,
            String sexo,
            String peso) {

        super(nombreDueño, nombreMascota, "Pájaro", vacunas,
                fechasVacunas, proximaVacuna, sexo, peso);
        this.especiePajaro = especiePajaro;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Información del pájaro");
        System.out.println("Dueño: " + nombreDueño);
        System.out.println("Mascota: " + nombreMascota);
        System.out.println("Especie del pájaro: " + especiePajaro);
        System.out.println("Sexo: " + sexo);
        System.out.println("Vacunas: " + vacunas);
        System.out.println("Fecha de vacunación: " + fechasVacunas);
        System.out.println("Próxima vacuna: " + proximaVacuna);
        System.out.println("Peso: " + peso);
    }
}

interface registro {
    void registrarMascota();
    void consultarMascota();
}

public class veterinaria implements registro {

    private final Scanner scanner = new Scanner(System.in);
    private final ArrayList<veterinario> mascotasRegistradas =
            new ArrayList<>();

    private final Path archivoMascotas = buscarArchivo();

    private static Path buscarArchivo() {
        Path local = Path.of("database", "mascotas.json");
        Path superior = Path.of("..", "database", "mascotas.json");

        if (Files.isDirectory(Path.of("database"))) {
            return local;
        }

        return superior;
    }

    private synchronized String leerMascotas() throws IOException {
        if (!Files.exists(archivoMascotas)) {
            Files.createDirectories(archivoMascotas.getParent());
            Files.writeString(
                    archivoMascotas,
                    "[]",
                    StandardCharsets.UTF_8
            );
        }

        return Files.readString(
                archivoMascotas,
                StandardCharsets.UTF_8
        );
    }

    private synchronized void guardarMascota(String json)
            throws IOException {

        String contenido = leerMascotas().trim();

        if (!contenido.startsWith("[")
                || !contenido.endsWith("]")) {
            throw new IOException("El archivo JSON no es válido.");
        }

        String interior = contenido.substring(
                1, contenido.length() - 1
        ).trim();

        String nuevoContenido;

        if (interior.isEmpty()) {
            nuevoContenido = "[" + json + "]";
        } else {
            nuevoContenido = "[" + interior + "," + json + "]";
        }

        Files.writeString(
                archivoMascotas,
                nuevoContenido,
                StandardCharsets.UTF_8
        );
    }

    private void responder(
            HttpExchange exchange,
            int estado,
            String cuerpo) throws IOException {

        byte[] datos = cuerpo.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );

        exchange.sendResponseHeaders(estado, datos.length);

        try (OutputStream salida = exchange.getResponseBody()) {
            salida.write(datos);
        }
    }

    public void iniciarServidor() throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/api/mascotas", exchange -> {
            try {
                String metodo = exchange.getRequestMethod();

                if (metodo.equalsIgnoreCase("OPTIONS")) {
                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Origin", "*"
                    );
                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Headers",
                            "Content-Type"
                    );
                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Methods",
                            "GET, POST, OPTIONS"
                    );

                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                if (metodo.equalsIgnoreCase("GET")) {
                    responder(exchange, 200, leerMascotas());
                    return;
                }

                if (metodo.equalsIgnoreCase("POST")) {
                    String cuerpo = new String(
                            exchange.getRequestBody().readAllBytes(),
                            StandardCharsets.UTF_8
                    ).trim();

                    if (!cuerpo.startsWith("{")
                            || !cuerpo.endsWith("}")) {
                        responder(
                                exchange,
                                400,
                                "{\"error\":\"JSON inválido\"}"
                        );
                        return;
                    }

                    guardarMascota(cuerpo);

                    System.out.println(
                            "Mascota guardada en: "
                                    + archivoMascotas.toAbsolutePath()
                    );

                    responder(
                            exchange,
                            201,
                            "{\"mensaje\":\"Mascota guardada correctamente\"}"
                    );
                    return;
                }

                responder(
                        exchange,
                        405,
                        "{\"error\":\"Método no permitido\"}"
                );

            } catch (Exception error) {
                error.printStackTrace();

                try {
                    responder(
                            exchange,
                            500,
                            "{\"error\":\"Error al procesar la solicitud\"}"
                    );
                } catch (IOException ignorado) {
                    exchange.close();
                }
            }
        });

        server.start();

        System.out.println(
                "Servidor iniciado en http://localhost:8080"
        );

        System.out.println(
                "Archivo de datos: "
                        + archivoMascotas.toAbsolutePath()
        );
    }

    @Override
    public void registrarMascota() {
        System.out.println(
                "El registro se realiza desde el formulario web."
        );
    }

    @Override
    public void consultarMascota() {
        try {
            System.out.println(leerMascotas());
        } catch (IOException error) {
            System.out.println(
                    "No se pudieron consultar las mascotas."
            );
        }
    }

    public static void main(String[] args) {
        veterinaria sistema = new veterinaria();

        try {
            sistema.iniciarServidor();
        } catch (IOException error) {
            System.err.println(
                    "No se pudo iniciar el servidor: "
                            + error.getMessage()
            );
        }
    }
}
