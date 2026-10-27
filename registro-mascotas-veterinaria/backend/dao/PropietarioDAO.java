package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PropietarioDAO {

    public boolean guardar(
            String nombre,
            String apellido,
            String telefono,
            String correo,
            String direccion) {

        String sql = "INSERT INTO propietarios "
                + "(nombre, apellido, telefono, correo, direccion) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, nombre);
            consulta.setString(2, apellido);
            consulta.setString(3, telefono);
            consulta.setString(4, correo);
            consulta.setString(5, direccion);

            return consulta.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(
                    "Error al guardar propietario: " + e.getMessage());
            return false;
        }
    }
}