package com.tienda.deportes.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tienda.deportes.model.Producto;

public class ProductoDAO {
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, marca, precio, stock, imagen FROM Productos";
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setMarca(rs.getString("marca"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
                producto.setImagen(rs.getString("imagen"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public boolean agregarProducto(Producto producto) {
        String sql = "INSERT INTO Productos (nombre, descripcion, marca, precio, stock, imagen) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setString(3, producto.getMarca());
            pstmt.setDouble(4, producto.getPrecio());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getImagen());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE Productos SET nombre = ?, descripcion = ?, marca = ?, precio = ?, stock = ?, imagen = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setString(3, producto.getMarca());
            pstmt.setDouble(4, producto.getPrecio());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getImagen());
            pstmt.setInt(7, producto.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM Productos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}