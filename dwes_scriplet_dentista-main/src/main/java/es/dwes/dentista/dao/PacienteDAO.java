package es.dwes.dentista.dao;

import es.dwes.dentista.config.ConexionBD;
import es.dwes.dentista.model.Paciente;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {
    public List<Paciente> listarTodos() throws SQLException {
        String sql = "SELECT p.id_paciente, p.nombre, p.apellidos, p.dni, p.fecha_nacimiento, "
                + "p.telefono, p.email, p.direccion, p.alergias, "
                + "(SELECT COUNT(*) FROM cita c WHERE c.id_paciente = p.id_paciente) AS numero_citas "
                + "FROM paciente p ORDER BY p.apellidos, p.nombre";
        List<Paciente> pacientes = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultados = sentencia.executeQuery(sql)) {
            while (resultados.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(resultados.getInt("id_paciente"));
                paciente.setNombre(resultados.getString("nombre"));
                paciente.setApellidos(resultados.getString("apellidos"));
                paciente.setDni(resultados.getString("dni"));
                paciente.setFechaNacimiento(resultados.getString("fecha_nacimiento"));
                paciente.setTelefono(resultados.getString("telefono"));
                paciente.setEmail(resultados.getString("email"));
                paciente.setDireccion(resultados.getString("direccion"));
                paciente.setAlergias(resultados.getString("alergias"));
                paciente.setNumeroCitas(resultados.getInt("numero_citas"));
                pacientes.add(paciente);
            }
        }
        return pacientes;
    }

    public List<Paciente> listarParaSelector() throws SQLException {
        String sql = "SELECT id_paciente, nombre, apellidos FROM paciente ORDER BY apellidos, nombre";
        List<Paciente> pacientes = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultados = sentencia.executeQuery(sql)) {
            while (resultados.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(resultados.getInt("id_paciente"));
                paciente.setNombre(resultados.getString("nombre"));
                paciente.setApellidos(resultados.getString("apellidos"));
                pacientes.add(paciente);
            }
        }
        return pacientes;
    }

    public void insertar(String nombre, String apellidos, String dni, LocalDate fechaNacimiento,
            String telefono, String email, String direccion, String alergias) throws SQLException {
        String sql = "INSERT INTO paciente "
                + "(nombre, apellidos, dni, fecha_nacimiento, telefono, email, direccion, alergias) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, dni);
            if (fechaNacimiento == null) {
                sentencia.setNull(4, Types.DATE);
            } else {
                sentencia.setDate(4, Date.valueOf(fechaNacimiento));
            }
            sentencia.setString(5, telefono);
            sentencia.setString(6, email);
            sentencia.setString(7, direccion);
            sentencia.setString(8, alergias);
            sentencia.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     "DELETE FROM paciente WHERE id_paciente = ?")) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }
}
