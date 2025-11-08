package co.edu.unicauca.servicios.config;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class ConexionBD {

    private static final String URL = "jdbc:h2:file:./data/testdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private Connection connection;

    public ConexionBD() {}

    /**
     * Establece una conexión a la base de datos.
     */
    public Connection conectar() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al conectar: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver H2: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Retorna la conexión activa.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    /**
     * Cierra la conexión si está abierta.
     */
    public void desconectar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
