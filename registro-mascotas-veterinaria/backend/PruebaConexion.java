import conexion.Conexion;
import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        try (Connection conexion = Conexion.conectar()) {
            System.out.println("¡Conexión exitosa con MySQL!");
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}