package com.tienda.deportes.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tienda.deportes.model.Producto;

public class CarritoDAO {
    private static final String ERROR_DB = "Error al acceder a la base de datos";

    public boolean agregarProductoAlCarrito(int usuarioId, int productoId, int cantidad) {
        if (cantidad <= 0) return false;

        String checkSql = "SELECT cantidad FROM Carrito WHERE usuario_id = ? AND producto_id = ?";
        String updateSql = "UPDATE Carrito SET cantidad = cantidad + ? WHERE usuario_id = ? AND producto_id = ?";
        String insertSql = "INSERT INTO Carrito (usuario_id, producto_id, cantidad) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Verificar si ya existe
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, usuarioId);
                checkStmt.setInt(2, productoId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, cantidad);
                        updateStmt.setInt(2, usuarioId);
                        updateStmt.setInt(3, productoId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, usuarioId);
                        insertStmt.setInt(2, productoId);
                        insertStmt.setInt(3, cantidad);
                        insertStmt.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + ": " + e.getMessage());
            return false;
        }
    }

    public List<Producto> obtenerProductosDelCarrito(int usuarioId) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.descripcion, p.marca, p.precio, p.stock, p.imagen, c.cantidad " +
                     "FROM Productos p JOIN Carrito c ON p.id = c.producto_id WHERE c.usuario_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setMarca(rs.getString("marca"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
                producto.setImagen(rs.getString("imagen"));
                producto.setCantidadEnCarrito(rs.getInt("cantidad"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al obtener carrito: " + e.getMessage());
        }
        return productos;
    }

    public boolean actualizarCantidadProducto(int usuarioId, int productoId, int cantidad) {
        if (cantidad <= 0) {
            return eliminarProductoDelCarrito(usuarioId, productoId);
        }

        String sql = "UPDATE Carrito SET cantidad = ? WHERE usuario_id = ? AND producto_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, usuarioId);
            pstmt.setInt(3, productoId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al actualizar cantidad: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProductoDelCarrito(int usuarioId, int productoId) {
        String sql = "DELETE FROM Carrito WHERE usuario_id = ? AND producto_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, productoId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean limpiarCarrito(int usuarioId) {
        String sql = "DELETE FROM Carrito WHERE usuario_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al limpiar carrito: " + e.getMessage());
            return false;
        }
    }
}