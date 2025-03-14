package com.tienda.deportes.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public boolean validarUsuario(String nombre, String password) {
        String sql = "SELECT * FROM Usuario WHERE nombre = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al validar usuario:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarUsuario(String nombre, String password) {
        String sql = "INSERT INTO Usuario (nombre, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, password);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeUsuario(String nombre) {
        String sql = "SELECT * FROM Usuario WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar si el usuario existe:");
            e.printStackTrace();
            return false;
        }
    }
}