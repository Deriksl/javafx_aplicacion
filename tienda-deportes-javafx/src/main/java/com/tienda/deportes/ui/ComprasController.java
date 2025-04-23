package com.tienda.deportes.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.DetalleVenta;
import com.tienda.deportes.model.Sesion;
import com.tienda.deportes.model.Venta;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ComprasController {
    @FXML private TableView<Venta> comprasTable;
    @FXML private TableColumn<Venta, LocalDateTime> fechaColumn;
    @FXML private TableColumn<Venta, Double> totalColumn;
    
    @FXML private TableView<DetalleVenta> detallesTable;
    @FXML private TableColumn<DetalleVenta, String> productoColumn;
    @FXML private TableColumn<DetalleVenta, Integer> cantidadColumn;
    @FXML private TableColumn<DetalleVenta, Double> precioColumn;
    @FXML private TableColumn<DetalleVenta, Double> subtotalColumn;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarTablas();
        cargarCompras();
    }

    private void configurarTablas() {
        // Configurar tabla de compras
        fechaColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        totalColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        
        fechaColumn.setCellFactory(column -> new TableCell<Venta, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(dateFormatter));
            }
        });
        
        totalColumn.setCellFactory(column -> new TableCell<Venta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Configurar tabla de detalles
        productoColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getNombreProducto()));
        cantidadColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getCantidad()));
        precioColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getPrecioUnitario()).asObject());
        subtotalColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(
                cellData.getValue().getPrecioUnitario() * 
                cellData.getValue().getCantidad()
            ).asObject());
        
        precioColumn.setCellFactory(column -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });
        
        subtotalColumn.setCellFactory(column -> new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        // Listener para cuando seleccionan una compra
        comprasTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    cargarDetallesCompra(newSelection.getId());
                }
            });
    }

    private void cargarCompras() {
        ObservableList<Venta> ventas = FXCollections.observableArrayList(
            ventaDAO.obtenerVentasPorUsuario(Sesion.getUsuarioId())
        );
        comprasTable.setItems(ventas);
        
        // Seleccionar la primera compra si existe
        if (!ventas.isEmpty()) {
            comprasTable.getSelectionModel().selectFirst();
        }
    }

    private void cargarDetallesCompra(int ventaId) {
        ObservableList<DetalleVenta> detalles = FXCollections.observableArrayList(
            ventaDAO.obtenerDetallesVenta(ventaId)
        );
        detallesTable.setItems(detalles);
    }

    @FXML
    private void regresarAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/menu.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) comprasTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}