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

    public void realizarVenta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO Ventas (usuario_id, fecha, total) VALUES (?, NOW(), ?)";
        String sqlDetalle = "INSERT INTO DetalleVentas (venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE Productos SET stock = stock - ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                pstmtVenta.setInt(1, venta.getUsuarioId());
                pstmtVenta.setDouble(2, venta.getTotal());
                pstmtVenta.executeUpdate();

                ResultSet rs = pstmtVenta.getGeneratedKeys();
                if (rs.next()) {
                    int ventaId = rs.getInt(1);

                    try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                        for (DetalleVenta detalle : detalles) {
                            pstmtDetalle.setInt(1, ventaId);
                            pstmtDetalle.setInt(2, detalle.getProductoId());
                            pstmtDetalle.setInt(3, detalle.getCantidad());
                            pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                            pstmtDetalle.executeUpdate();
                        }
                    }

                    try (PreparedStatement pstmtStock = conn.prepareStatement(sqlActualizarStock)) {
                        for (DetalleVenta detalle : detalles) {
                            pstmtStock.setInt(1, detalle.getCantidad());
                            pstmtStock.setInt(2, detalle.getProductoId());
                            pstmtStock.executeUpdate();
                        }
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                venta.setTotal(rs.getDouble("total"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

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
            e.printStackTrace();
        }
        return ventas;
    }
}