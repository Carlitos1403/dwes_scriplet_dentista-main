package es.dwes.dentista.dao;

import es.dwes.dentista.config.ConexionBD;
import es.dwes.dentista.model.Odontologo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OdontologoDAO {
    public List<Odontologo> listarTodos() throws SQLException {
        List<Odontologo> odontologos = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultados = sentencia.executeQuery(
                     "SELECT id_odontologo, nombre, especialidad FROM odontologos ORDER BY nombre")) {
            while (resultados.next()) {
                Odontologo odontologo = new Odontologo();
                odontologo.setId(resultados.getInt("id_odontologo"));
                odontologo.setNombre(resultados.getString("nombre"));
                odontologo.setEspecialidad(resultados.getString("especialidad"));
                odontologos.add(odontologo);
            }
        }
        return odontologos;
    }
}
