package com.tienda.deportes.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tienda_deportes";
    private static final String USER = "Derik"; // Cambia por tu usuario de MySQL
    private static final String PASSWORD = "Noviembre123"; // Cambia por tu contraseña de MySQL

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Método main temporal para probar la conexión
    public static void main(String[] args) {
        System.out.println("Probando conexión a la base de datos...");

        try (Connection conn = getConnection()) {
            System.out.println("¡Conexión exitosa a la base de datos!");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos:");
            e.printStackTrace(); // Imprime el error detallado
        }
    }
}