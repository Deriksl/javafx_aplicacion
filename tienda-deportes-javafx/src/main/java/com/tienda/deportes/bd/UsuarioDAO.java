package com.tienda.deportes.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tienda.deportes.model.Usuario;

public class UsuarioDAO {
    private static final String ERROR_DB = "[Error DB]";

    // Método para validar credenciales (Login)
    public boolean validarUsuario(String nombre, String password) {
        String sql = "SELECT id FROM Usuario WHERE nombre = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, password);
            
            System.out.println(ERROR_DB + " Validando usuario: " + pstmt);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // True si encuentra coincidencia
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al validar: " + e.getMessage());
            return false;
        }
    }

    // Obtener tipo de usuario (admin/cliente)
    public String obtenerTipoUsuario(String nombre) {
        String sql = "SELECT tipo FROM Usuario WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipo");
                }
                return "cliente"; // Valor por defecto si no encuentra
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al obtener tipo: " + e.getMessage());
            return "cliente";
        }
    }

    // Obtener ID de usuario
    public int obtenerIdUsuario(String nombre) {
        String sql = "SELECT id FROM Usuario WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                return 0; // Retorna 0 si no encuentra
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al obtener ID: " + e.getMessage());
            return 0;
        }
    }

    // Registrar nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuario (nombre, password, tipo) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getTipo());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al registrar: " + e.getMessage());
            return false;
        }
    }

    // Verificar si usuario ya existe
    public boolean existeUsuario(String nombre) {
        String sql = "SELECT id FROM Usuario WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al verificar usuario: " + e.getMessage());
            return true; // Por seguridad, asume que existe si hay error
        }
    }

    // Obtener todos los usuarios (para gestión)
    public List<Usuario> obtenerTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, password, tipo FROM Usuario ORDER BY nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPassword(rs.getString("password"));
                usuario.setTipo(rs.getString("tipo"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    // Actualizar usuario existente
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE Usuario SET nombre = ?, password = ?, tipo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getTipo());
            pstmt.setInt(4, usuario.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // Eliminar usuario
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT id, nombre, password, tipo FROM Usuario WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setTipo(rs.getString("tipo"));
                    return usuario;
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " Error al obtener por ID: " + e.getMessage());
            return null;
        }
    }
}