/*La solución es  un sistema de registro de las mascotas que nos permita almacenar y 
consultar la información de .
• nombre y datos de la mascotas 
• información del dueño
• vacunas aplicada 
• fecha de vacunación
• próxima fecha de vacunas
• historial de las mascota*/
//dueño
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.ArrayList;

abstract class veterinario {
    protected String nombreDueño;
    protected String nombreMascota;
    protected String especie;
    protected String vacunas;
    protected String fechasVacunas;
    protected String proximaVacuna;
    protected String sexo;
    protected String peso;

    public veterinario(String nombreDueño, String nombreMascota, String especie, String vacunas, String fechasVacunas,
            String proximaVacuna, String sexo, String peso) {
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

    public Perro(String nombreDueño, String nombreMascota, String raza, String vacunas, String fechasVacunas,
            String proximaVacuna, String sexo, String especie, String peso) {
        super(nombreDueño, nombreMascota, especie, vacunas, fechasVacunas, proximaVacuna, sexo, peso);
        this.raza = raza;

    }

    @Override
    public void mostrarInformacion() {
        System.out.println("informcion del perro");
        System.out.println("dueño: " + nombreDueño);
        System.out.println("Mascota: " + nombreMascota + " | Raza: " + raza + " | Sexo: " + sexo);
        System.out.println("especie: " + especie);// puede ser que lo elimine
        System.out.println("vacunas: " + vacunas);
        System.out.println("fecha de vacuna: " + fechasVacunas);
        System.out.println("próxima vacuna: " + proximaVacuna);
        System.out.println("peso: " + peso);
    }
}

class Gato extends veterinario {
    private String raza;

    public Gato(String nombreDueño, String nombreMascota, String raza, String vacunas, String fechasVacunas,
            String proximaVacuna, String sexo, String especie, String peso) {
        super(nombreDueño, nombreMascota, especie, vacunas, fechasVacunas, proximaVacuna, sexo, peso);
        this.raza = raza;

    }

    @Override
    public void mostrarInformacion() {
        System.out.println("informcion del gato");
        System.out.println("dueño: " + nombreDueño);
        System.out.println("mascota: " + nombreMascota + " | Raza: " + raza + " | Sexo: " + sexo);
        System.out.println("especie: " + especie);
        System.out.println("vacunas: " + vacunas);
        System.out.println("fecha de vacuna: " + fechasVacunas);
        System.out.println("próxima vacuna: " + proximaVacuna);
        System.out.println("peso: " + peso);
    }
}

class Pajaros extends veterinario {
    public Pajaros(String nombreDueño, String nombreMascota, String especie, String vacunas, String fechasVacunas,
            String proximaVacuna, String sexo, String especiePajaros, String peso) {
        super(nombreDueño, nombreMascota, especie, vacunas, fechasVacunas, proximaVacuna, sexo, peso);
        this.especie = especiePajaros;

    }

    @Override
    public void mostrarInformacion() {
        System.out.println("informcion del pajaros");
        System.out.println("dueño: " + nombreDueño);
        System.out.println("mascota: " + nombreMascota + "sexo: " + sexo);
        System.out.println("vacunas: " + vacunas);
        System.out.println("especie: " + especie);
        System.out.println("fecha de vacuna: " + fechasVacunas);
        System.out.println("próxima vacuna: " + proximaVacuna);

    }
}

interface registro {
    void registrarMascota();

    void consultarMascota();

}

public class veterinaria implements registro {
    private Scanner scanner = new Scanner(System.in);
    private ArrayList<veterinario> mascotasRegistradas = new ArrayList<>();

    @Override
    public void registrarMascota() {
        System.out.println(" registro de mascotas ");
        System.out.println("Ingrese el nombre del dueño: ");
        String nombreDueño = scanner.nextLine();
        System.out.println("Ingrese el nombre de la mascota: ");
        String nombreMascota = scanner.nextLine();
        System.out.println("ESPECIE DE LA MASCOTA: ");
        String especie = scanner.nextLine();
        System.out.println("vacunas aplicadas: ");
        String vacunas = scanner.nextLine();
        System.out.println("fecha de vacunación: ");
        String fechaVacuna = scanner.nextLine();
        System.out.println("próxima fecha de vacunación: ");
        String proximaVacuna = scanner.nextLine();
        System.out.println("sexo de la mascota: ");
        String sexo = scanner.nextLine();
        veterinario nueva = null;

        switch (especie) {
            case "perro":
                System.out.println("Ingrese la raza del perro: ");
                String razaPerro = scanner.nextLine();
                System.out.println("Ingrese el peso de la mascota: ");
                String peso = scanner.nextLine();
                nueva = new Perro(nombreDueño, nombreMascota, especie, vacunas, fechaVacuna, proximaVacuna, sexo,
                        razaPerro, peso);
                break;
            case "gato":
                System.out.println("Ingrese la raza del gato: ");
                String razaGato = scanner.nextLine();
                System.out.println("Ingrese el peso de la mascota: ");
                String pesoGato = scanner.nextLine();
                nueva = new Gato(nombreDueño, nombreMascota, especie, vacunas, fechaVacuna, proximaVacuna, sexo,
                        razaGato, pesoGato);
                break;

            case "pajaros":
                System.out.println("Ingrese la especie del pájaro: ");
                String especiePajaro = scanner.nextLine();
                System.out.println("Ingrese el peso de la mascota: ");
                String pesoPajaro = scanner.nextLine();
                nueva = new Pajaros(nombreDueño, nombreMascota, especie, vacunas, fechaVacuna, proximaVacuna, sexo,
                        especiePajaro, pesoPajaro);
                break;
            default:
                System.out.println("Especie no válida ");
                return;
        }
        mascotasRegistradas.add(nueva);
        System.out.println("Mascota registrada exitosamente.");
    }

    @Override
    public void consultarMascota() {
        if (mascotasRegistradas.isEmpty()) {
            System.out.println("No hay mascotas registradas.");
        } else {
            for (veterinario v : mascotasRegistradas) {
                v.mostrarInformacion();
            }

        }
    }
    public void iniciarServidor() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    server.createContext("/api/mascotas", (HttpExchange exchange) -> {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            String body = new String(exchange.getRequestBody().readAllBytes());

            System.out.println("Datos recibidos:");
            System.out.println(body);

            String respuesta = "{\"mensaje\":\"Mascota registrada correctamente\"}";

            exchange.getResponseHeaders().add("Content-Type", "application/json");

            exchange.sendResponseHeaders(200, respuesta.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(respuesta.getBytes());
            os.close();
        }
    });

    server.start();

    System.out.println("Servidor iniciado en http://localhost:8080");
}
   /*  public static void main(String[] args) {
        veterinaria sistema = new veterinaria();
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("Sistema de Registro de Mascotas");
            System.out.println("1. Registrar Mascota");
            System.out.println("2. Consultar Mascota");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer
            switch (opcion) {
                case 1:
                    sistema.registrarMascota();
                    break;
                case 2:
                    sistema.consultarMascota();
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 3);
        scanner.close();
    }
}*/
public static void main(String[] args) throws IOException {

    veterinaria sistema = new veterinaria();

    sistema.iniciarServidor();

    System.out.println("Backend ejecutándose...");
}
