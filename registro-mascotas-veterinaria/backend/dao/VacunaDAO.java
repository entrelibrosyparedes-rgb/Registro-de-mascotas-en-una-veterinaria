package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VacunaDAO {

    public boolean guardar(
            int mascotaId,
            String nombre,
            String fechaAplicacion,
            String proximaFecha,
            String observaciones) {

        String sql = "INSERT INTO vacunas "
                + "(mascota_id, nombre, fechaAplicacion, proximaFecha, observaciones) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setInt(1, mascotaId);
            consulta.setString(2, nombre);
            consulta.setDate(3, Date.valueOf(fechaAplicacion));

            if (proximaFecha == null || proximaFecha.isBlank()) {
                consulta.setNull(4, java.sql.Types.DATE);
            } else {
                consulta.setDate(4, Date.valueOf(proximaFecha));
            }

            consulta.setString(5, observaciones);

            return consulta.executeUpdate() > 0;

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println(
                    "Error al guardar vacuna: " + e.getMessage());
            return false;
        }
    }
}