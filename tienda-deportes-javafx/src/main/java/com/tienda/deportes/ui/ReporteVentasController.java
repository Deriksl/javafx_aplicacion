package com.tienda.deportes.ui;

import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;
import com.tienda.deportes.model.Venta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReporteVentasController {
    @FXML private TableView<Venta> ventasTable;
    @FXML private TableColumn<Venta, String> fechaColumn;
    @FXML private TableColumn<Venta, Double> totalColumn;

    private VentaDAO ventaDAO = new VentaDAO();

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
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    private void cargarVentas() {
        ObservableList<Venta> ventas = FXCollections.observableArrayList(ventaDAO.obtenerTodasLasVentas());
        ventasTable.setItems(ventas);
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
}