package com.tienda.deportes.ui;

import java.util.List;

import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.Venta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ComprasController {
    @FXML
    private TableView<Venta> comprasTable;
    @FXML
    private TableColumn<Venta, String> fechaColumn;
    @FXML
    private TableColumn<Venta, Double> totalColumn;

    private VentaDAO ventaDAO = new VentaDAO();

    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Cargar las compras del usuario
        cargarCompras();
    }

    private void cargarCompras() {
        List<Venta> compras = ventaDAO.obtenerVentasPorUsuario(1); // Suponiendo que el usuario tiene id=1
        ObservableList<Venta> comprasObservableList = FXCollections.observableArrayList(compras);
        comprasTable.setItems(comprasObservableList);
    }

    @FXML
    private void regresarAlMenu() {
        // Código para regresar al menú
        try {
            // Cargar la ventana del menú
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/menu.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) comprasTable.getScene().getWindow();

            // Reemplazar la escena actual con la escena del menú
            stage.setScene(new Scene(root));
            stage.setTitle("Menú Principal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}