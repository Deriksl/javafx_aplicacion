package com.tienda.deportes.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tienda.deportes.model.DetalleVenta;
import com.tienda.deportes.model.Venta;

public class VentaDAO {
    private static final String ERROR_DB = "Error al acceder a la base de datos";

    /**
     * Registra una nueva venta en el sistema con sus detalles correspondientes
     * @param venta Objeto Venta con los datos principales
     * @param detalles Lista de DetalleVenta con los productos vendidos
     */
    public void realizarVenta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO Ventas (usuario_id, fecha, total) VALUES (?, NOW(), ?)";
        String sqlDetalle = "INSERT INTO DetalleVentas (venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE Productos SET stock = stock - ? WHERE id = ?";
        String sqlLimpiarCarrito = "DELETE FROM Carrito WHERE usuario_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertar la venta principal
            try (PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                pstmtVenta.setInt(1, venta.getUsuarioId());
                pstmtVenta.setDouble(2, venta.getTotal());
                pstmtVenta.executeUpdate();

                // Obtener el ID de la venta recién insertada
                ResultSet rs = pstmtVenta.getGeneratedKeys();
                if (rs.next()) {
                    int ventaId = rs.getInt(1);

                    // 2. Insertar los detalles de la venta
                    try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                        for (DetalleVenta detalle : detalles) {
                            pstmtDetalle.setInt(1, ventaId);
                            pstmtDetalle.setInt(2, detalle.getProductoId());
                            pstmtDetalle.setInt(3, detalle.getCantidad());
                            pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                            pstmtDetalle.executeUpdate();
                        }
                    }

                    // 3. Actualizar el stock de productos
                    try (PreparedStatement pstmtStock = conn.prepareStatement(sqlActualizarStock)) {
                        for (DetalleVenta detalle : detalles) {
                            pstmtStock.setInt(1, detalle.getCantidad());
                            pstmtStock.setInt(2, detalle.getProductoId());
                            pstmtStock.executeUpdate();
                        }
                    }

                    // 4. Limpiar el carrito del usuario
                    try (PreparedStatement pstmtCarrito = conn.prepareStatement(sqlLimpiarCarrito)) {
                        pstmtCarrito.setInt(1, venta.getUsuarioId());
                        pstmtCarrito.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println(ERROR_DB + " al hacer rollback: " + ex.getMessage());
                }
            }
            throw new RuntimeException(ERROR_DB + " al realizar venta: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println(ERROR_DB + " al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Obtiene el historial de ventas de un usuario específico
     * @param usuarioId ID del usuario
     * @return Lista de ventas ordenadas por fecha (más reciente primero)
     */
    public List<Venta> obtenerVentasPorUsuario(int usuarioId) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha, total FROM Ventas WHERE usuario_id = ? ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("id"));
                venta.setUsuarioId(usuarioId);
                venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                venta.setTotal(rs.getDouble("total"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al obtener ventas por usuario: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtiene todas las ventas del sistema (para administradores)
     * @return Lista completa de ventas ordenadas por fecha (más reciente primero)
     */
    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, usuario_id, fecha, total FROM Ventas ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId(rs.getInt("id"));
                venta.setUsuarioId(rs.getInt("usuario_id"));
                venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                venta.setTotal(rs.getDouble("total"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al obtener todas las ventas: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtiene los detalles de una venta específica
     * @param ventaId ID de la venta
     * @return Lista de detalles de la venta
     */
    public List<DetalleVenta> obtenerDetallesVenta(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.producto_id, p.nombre, dv.cantidad, dv.precio_unitario " +
                     "FROM DetalleVentas dv JOIN Productos p ON dv.producto_id = p.id " +
                     "WHERE dv.venta_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ventaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVentaId(ventaId);
                detalle.setProductoId(rs.getInt("producto_id"));
                detalle.setCantidad(rs.getInt("cantidad"));
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            System.err.println(ERROR_DB + " al obtener detalles de venta: " + e.getMessage());
        }
        return detalles;
    }
}