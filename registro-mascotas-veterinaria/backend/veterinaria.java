import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dao.MascotaDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class veterinaria {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final MascotaDAO dao = new MascotaDAO();

    private static void responder(
            HttpExchange exchange,
            int estado,
            Object contenido) throws IOException {

        byte[] datos = mapper.writeValueAsBytes(contenido);

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
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
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
                            "Access-Control-Allow-Headers", "Content-Type"
                    );
                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
                    );
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
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
                            new TypeReference<Map<String, Object>>() {}
                    );

                    if (mascota.get("nombre") == null
                            || mascota.get("especie") == null
                            || mascota.get("nombre").toString().isBlank()
                            || mascota.get("especie").toString().isBlank()) {
                        responder(exchange, 400,
                                Map.of("error", "El nombre y la especie son obligatorios"));
                        return;
                    }

                    boolean guardada = dao.guardar(mascota);

                    if (guardada) {
                        responder(exchange, 201,
                                Map.of("mensaje", "Mascota guardada correctamente"));
                    } else {
                        responder(exchange, 500,
                                Map.of("error", "No se pudo guardar la mascota"));
                    }
                    return;
                }

                responder(exchange, 405,
                        Map.of("error", "Método no permitido"));

            } catch (SQLException error) {
                error.printStackTrace();
                responder(exchange, 500,
                        Map.of("error", "Error en la base de datos: "
                                + error.getMessage()));

            } catch (Exception error) {
                error.printStackTrace();
                responder(exchange, 400,
                        Map.of("error", "No se pudo procesar la solicitud: "
                                + error.getMessage()));
            }
        });

        server.start();
        System.out.println("Servidor iniciado en http://localhost:8080");
        System.out.println("Base de datos: veterinaria");
    }

    public static void main(String[] args) {
        try {
            new veterinaria().iniciarServidor();
        } catch (IOException error) {
            System.err.println(
                    "No se pudo iniciar el servidor: " + error.getMessage()
            );
        }
    }
}
