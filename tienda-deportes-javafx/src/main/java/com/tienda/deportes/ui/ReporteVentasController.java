package com.tienda.deportes.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.DetalleVenta;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;
import com.tienda.deportes.model.Venta;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReporteVentasController {
    @FXML private TableView<Venta> ventasTable;
    @FXML private TableColumn<Venta, String> fechaColumn;
    @FXML private TableColumn<Venta, String> clienteColumn;
    @FXML private TableColumn<Venta, String> productosColumn;
    @FXML private TableColumn<Venta, Double> totalColumn;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        if (!Sesion.esAdmin()) {
            mostrarAlertaYRegresar();
            return;
        }
        
        configurarTabla();
        cargarVentas();
    }

    private void configurarTabla() {
        fechaColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFecha().format(dateFormatter)));
        
        clienteColumn.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        
        productosColumn.setCellValueFactory(cellData -> {
            List<DetalleVenta> detalles = ventaDAO.obtenerDetallesVenta(cellData.getValue().getId());
            if (detalles == null || detalles.isEmpty()) {
                return new SimpleStringProperty("No hay detalles disponibles");
            }
            
            StringBuilder productosStr = new StringBuilder();
            for (DetalleVenta detalle : detalles) {
                productosStr.append(String.format("%s (x%d), ", 
                    detalle.getNombreProducto(),
                    detalle.getCantidad()));
            }
            if (productosStr.length() > 0) {
                productosStr.setLength(productosStr.length() - 2);
            }
            return new SimpleStringProperty(productosStr.toString());
        });
        
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalColumn.setCellFactory(column -> new TableCell<Venta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });
    }

    private void cargarVentas() {
        try {
            List<Venta> ventas = ventaDAO.obtenerTodasLasVentas();
            if (ventas.isEmpty()) {
                mostrarAlerta("Información", "No hay ventas registradas aún.");
            } else {
                System.out.println("Ventas encontradas: " + ventas.size());
                for (Venta v : ventas) {
                    System.out.println("Venta ID: " + v.getId() + ", Cliente: " + v.getClienteNombre());
                }
            }
            ventasTable.setItems(FXCollections.observableArrayList(ventas));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar las ventas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarAlMenu() {
        try {
            Stage stage = (Stage) ventasTable.getScene().getWindow();
            SceneLoader.loadScene(stage, "/com/tienda/deportes/ui/menu.fxml", "Menú Principal");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo regresar al menú: " + e.getMessage());
        }
    }

    private void mostrarAlertaYRegresar() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acceso denegado");
        alert.setHeaderText(null);
        alert.setContentText("No tienes permisos para acceder a esta función");
        alert.showAndWait();
        regresarAlMenu();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}