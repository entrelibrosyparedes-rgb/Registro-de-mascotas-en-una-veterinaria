import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dao.MascotaDAO;
import dao.PropietarioDAO;
import dao.VacunaDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class veterinaria {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final MascotaDAO dao = new MascotaDAO();
    private static final PropietarioDAO propietarioDAO =
            new PropietarioDAO();
    private static final VacunaDAO vacunaDAO = new VacunaDAO();

    private static void configurarCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    }

    private static void responder(
            HttpExchange exchange,
            int estado,
            Object contenido) throws IOException {

        byte[] datos = mapper.writeValueAsBytes(contenido);

        configurarCORS(exchange);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(estado, datos.length);

        try (OutputStream salida = exchange.getResponseBody()) {
            salida.write(datos);
        }
    }

    private static void responderSinContenido(
            HttpExchange exchange) throws IOException {

        configurarCORS(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    private static String obtenerTexto(
            Map<String, Object> datos,
            String campo) {

        Object valor = datos.get(campo);

        return valor == null ? "" : valor.toString().trim();
    }

    public void iniciarServidor() throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        // ==========================================
        // API DE MASCOTAS
        // ==========================================

        server.createContext("/api/mascotas", exchange -> {
            try {
                String metodo = exchange.getRequestMethod();

                if (metodo.equalsIgnoreCase("OPTIONS")) {
                    responderSinContenido(exchange);
                    return;
                }

                if (metodo.equalsIgnoreCase("GET")) {
                    List<Map<String, Object>> mascotas = dao.listar();

                    responder(exchange, 200, mascotas);
                    return;
                }

                if (metodo.equalsIgnoreCase("POST")) {
                    Map<String, Object> mascota = mapper.readValue(
                            exchange.getRequestBody(),
                            new TypeReference<Map<String, Object>>() {});

                    String nombre = obtenerTexto(mascota, "nombre");
                    String especie = obtenerTexto(mascota, "especie");

                    if (nombre.isBlank() || especie.isBlank()) {
                        responder(exchange, 400,
                                Map.of("error",
                                        "El nombre y la especie son obligatorios"));
                        return;
                    }

                    boolean guardada = dao.guardar(mascota);

                    if (guardada) {
                        responder(exchange, 201,
                                Map.of("mensaje",
                                        "Mascota guardada correctamente"));
                    } else {
                        responder(exchange, 500,
                                Map.of("error",
                                        "No se pudo guardar la mascota"));
                    }

                    return;
                }

                responder(exchange, 405,
                        Map.of("error", "Método no permitido"));

            } catch (SQLException error) {
                error.printStackTrace();

                responder(exchange, 500,
                        Map.of("error",
                                "Error en la base de datos: "
                                        + error.getMessage()));

            } catch (Exception error) {
                error.printStackTrace();

                responder(exchange, 400,
                        Map.of("error",
                                "No se pudo procesar la solicitud: "
                                        + error.getMessage()));
            }
        });

        // ==========================================
        // API DE PROPIETARIOS
        // ==========================================

        server.createContext("/api/propietarios", exchange -> {
            try {
                String metodo = exchange.getRequestMethod();

                if (metodo.equalsIgnoreCase("OPTIONS")) {
                    responderSinContenido(exchange);
                    return;
                }

                if (!metodo.equalsIgnoreCase("POST")) {
                    responder(exchange, 405,
                            Map.of("error", "Método no permitido"));
                    return;
                }

                Map<String, Object> propietario = mapper.readValue(
                        exchange.getRequestBody(),
                        new TypeReference<Map<String, Object>>() {});

                String nombre = obtenerTexto(propietario, "nombre");
                String apellido = obtenerTexto(propietario, "apellido");
                String telefono = obtenerTexto(propietario, "telefono");
                String correo = obtenerTexto(propietario, "correo");
                String direccion = obtenerTexto(propietario, "direccion");

                if (nombre.isBlank() || apellido.isBlank()) {
                    responder(exchange, 400,
                            Map.of("error",
                                    "El nombre y el apellido son obligatorios"));
                    return;
                }

                boolean guardado = propietarioDAO.guardar(
                        nombre,
                        apellido,
                        telefono,
                        correo,
                        direccion);

                if (guardado) {
                    responder(exchange, 201,
                            Map.of("mensaje",
                                    "Propietario guardado correctamente"));
                } else {
                    responder(exchange, 500,
                            Map.of("error",
                                    "No se pudo guardar el propietario"));
                }

            } catch (Exception error) {
                error.printStackTrace();

                responder(exchange, 500,
                        Map.of("error",
                                "Error al guardar propietario: "
                                        + error.getMessage()));
            }
        });

        // ==========================================
        // API DE VACUNAS
        // ==========================================

        server.createContext("/api/vacunas", exchange -> {
            try {
                String metodo = exchange.getRequestMethod();

                if (metodo.equalsIgnoreCase("OPTIONS")) {
                    responderSinContenido(exchange);
                    return;
                }

                if (!metodo.equalsIgnoreCase("POST")) {
                    responder(exchange, 405,
                            Map.of("error", "Método no permitido"));
                    return;
                }

                Map<String, Object> vacuna = mapper.readValue(
                        exchange.getRequestBody(),
                        new TypeReference<Map<String, Object>>() {});

                String nombre = obtenerTexto(vacuna, "nombre");

                String fechaAplicacion =
                        obtenerTexto(vacuna, "fechaAplicacion");

                String proximaFecha =
                        obtenerTexto(vacuna, "proximaFecha");

                String observaciones =
                        obtenerTexto(vacuna, "observaciones");

                if (nombre.isBlank() || fechaAplicacion.isBlank()) {
                    responder(exchange, 400,
                            Map.of("error",
                                    "El nombre y la fecha de aplicación son obligatorios"));
                    return;
                }

                // Obtener el identificador de la mascota seleccionada.
                int mascotaId;

                try {
                    mascotaId = Integer.parseInt(
                            obtenerTexto(vacuna, "mascotaId"));
                } catch (NumberFormatException error) {
                    responder(exchange, 400,
                            Map.of("error",
                                    "Debes seleccionar una mascota válida"));
                    return;
                }

                if (mascotaId <= 0) {
                    responder(exchange, 400,
                            Map.of("error",
                                    "El identificador de la mascota no es válido"));
                    return;
                }

                // Guardar la vacuna asociada a la mascota.
                boolean guardada = vacunaDAO.guardar(
                        mascotaId,
                        nombre,
                        fechaAplicacion,
                        proximaFecha,
                        observaciones);

                if (guardada) {
                    responder(exchange, 201,
                            Map.of("mensaje",
                                    "Vacuna guardada correctamente"));
                } else {
                    responder(exchange, 500,
                            Map.of("error",
                                    "No se pudo guardar la vacuna. "
                                            + "Revisa la terminal de Java."));
                }

            } catch (Exception error) {
                error.printStackTrace();

                responder(exchange, 400,
                        Map.of("error",
                                "Error al procesar la vacuna: "
                                        + error.getMessage()));
            }
        });

        // ==========================================
        // INICIAR SERVIDOR
        // ==========================================

        server.start();

        System.out.println(
                "Servidor iniciado en http://localhost:8080");

        System.out.println(
                "API de mascotas: http://localhost:8080/api/mascotas");

        System.out.println(
                "API de propietarios: http://localhost:8080/api/propietarios");

        System.out.println(
                "API de vacunas: http://localhost:8080/api/vacunas");

        System.out.println("Base de datos: veterinaria");
    }

    public static void main(String[] args) {
        try {
            new veterinaria().iniciarServidor();
        } catch (IOException error) {
            System.err.println(
                    "No se pudo iniciar el servidor: "
                            + error.getMessage());
        }
    }
}