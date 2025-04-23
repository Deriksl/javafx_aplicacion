package com.tienda.deportes.ui;

import com.tienda.deportes.bd.ProductoDAO;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReporteInventarioController {
    @FXML private TableView<Producto> inventarioTable;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, String> descripcionColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;

    private ProductoDAO productoDAO = new ProductoDAO();

    @FXML
    public void initialize() {
        if (!Sesion.esAdmin()) {
            mostrarAlertaYRegresar();
            return;
        }
        
        configurarTabla();
        cargarInventario();
    }

    private void configurarTabla() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    private void cargarInventario() {
        ObservableList<Producto> productos = FXCollections.observableArrayList(productoDAO.obtenerTodos());
        inventarioTable.setItems(productos);
    }

    @FXML
    private void regresarAlMenu() {
        try {
            Stage stage = (Stage) inventarioTable.getScene().getWindow();
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