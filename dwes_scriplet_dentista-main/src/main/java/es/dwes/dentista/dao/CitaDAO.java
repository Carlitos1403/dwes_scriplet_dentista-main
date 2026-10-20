package es.dwes.dentista.dao;

import es.dwes.dentista.config.ConexionBD;
import es.dwes.dentista.model.Cita;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {
    public List<Cita> listar(boolean todas) throws SQLException {
        String sql = "SELECT c.id_cita, c.fecha, c.hora, c.motivo, "
                + "p.nombre AS nombre_paciente, p.apellidos, o.nombre AS nombre_odontologo "
                + "FROM cita c "
                + "JOIN paciente p ON p.id_paciente = c.id_paciente "
                + "JOIN odontologos o ON o.id_odontologo = c.id_odontologo "
                + (todas ? "" : "WHERE c.fecha >= CURDATE() ")
                + "ORDER BY c.fecha, c.hora";
        List<Cita> citas = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultados = sentencia.executeQuery()) {
            while (resultados.next()) {
                Cita cita = new Cita();
                cita.setId(resultados.getInt("id_cita"));
                cita.setFecha(resultados.getString("fecha"));
                String hora = resultados.getString("hora");
                cita.setHora(hora == null || hora.length() <= 5 ? hora : hora.substring(0, 5));
                cita.setPaciente(resultados.getString("apellidos") + ", "
                        + resultados.getString("nombre_paciente"));
                cita.setMotivo(resultados.getString("motivo"));
                cita.setOdontologo(resultados.getString("nombre_odontologo"));
                citas.add(cita);
            }
        }
        return citas;
    }

    public void insertar(LocalDate fecha, LocalTime hora, int idPaciente, int idOdontologo,
            String motivo) throws SQLException {
        String sql = "INSERT INTO cita (fecha, hora, id_paciente, id_odontologo, motivo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setDate(1, Date.valueOf(fecha));
            sentencia.setTime(2, Time.valueOf(hora));
            sentencia.setInt(3, idPaciente);
            sentencia.setInt(4, idOdontologo);
            if (motivo == null || motivo.isBlank()) {
                sentencia.setNull(5, Types.VARCHAR);
            } else {
                sentencia.setString(5, motivo);
            }
            sentencia.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM cita WHERE id_cita = ?")) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }
}
