package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MascotaDAO {

    public boolean guardar(Map<String, Object> mascota) throws SQLException {
        String sql = "INSERT INTO mascotas "
                + "(nombre, especie, raza, sexo, fechaNacimiento, peso, propietario) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, texto(mascota.get("nombre")));
            ps.setString(2, texto(mascota.get("especie")));
            ps.setString(3, texto(mascota.get("raza")));
            ps.setString(4, texto(mascota.get("sexo")));

            String fecha = texto(mascota.get("fechaNacimiento"));
            if (fecha.isEmpty()) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(fecha));
            }

            Object peso = mascota.get("peso");
            if (peso == null || texto(peso).isEmpty()) {
                ps.setNull(6, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(6, new java.math.BigDecimal(texto(peso)));
            }

            ps.setString(7, texto(mascota.get("propietario")));

            return ps.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> listar() throws SQLException {
        List<Map<String, Object>> mascotas = new ArrayList<>();

        String sql = "SELECT id, nombre, especie, raza, sexo, "
                + "fechaNacimiento, peso, propietario, fechaRegistro "
                + "FROM mascotas ORDER BY id DESC";

        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> mascota = new LinkedHashMap<>();

                mascota.put("id", rs.getInt("id"));
                mascota.put("nombre", rs.getString("nombre"));
                mascota.put("especie", rs.getString("especie"));
                mascota.put("raza", rs.getString("raza"));
                mascota.put("sexo", rs.getString("sexo"));

                Date fecha = rs.getDate("fechaNacimiento");
                mascota.put("fechaNacimiento",
                        fecha == null ? null : fecha.toString());

                java.math.BigDecimal peso = rs.getBigDecimal("peso");
                mascota.put("peso", peso);

                mascota.put("propietario", rs.getString("propietario"));
                mascota.put("fechaRegistro",
                        rs.getTimestamp("fechaRegistro").toString());

                mascotas.add(mascota);
            }
        }

        return mascotas;
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString().trim();
    }
}