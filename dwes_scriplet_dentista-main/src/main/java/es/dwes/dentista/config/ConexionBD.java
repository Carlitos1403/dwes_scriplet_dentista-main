package es.dwes.dentista.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {
    private static final String URL = System.getenv().getOrDefault(
            "DENTISTA_DB_URL",
            "jdbc:mysql://localhost:3306/dentista?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8");
    private static final String USUARIO = System.getenv().getOrDefault("DENTISTA_DB_USER", "user");
    private static final String CONTRASENA = System.getenv().getOrDefault("DENTISTA_DB_PASSWORD", "user");

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
