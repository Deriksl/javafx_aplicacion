package com.tienda.deportes.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.Sesion;
import com.tienda.deportes.model.Venta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ComprasController {
    @FXML private TableView<Venta> comprasTable;
    @FXML private TableColumn<Venta, LocalDateTime> fechaColumn;
    @FXML private TableColumn<Venta, Double> totalColumn;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCompras();
    }

    private void configurarTabla() {
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Formatear fecha
        fechaColumn.setCellFactory(column -> new TableCell<Venta, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(dateFormatter));
                }
            }
        });
        
        // Formatear total como moneda
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

    private void cargarCompras() {
        ObservableList<Venta> ventas = FXCollections.observableArrayList(
            ventaDAO.obtenerVentasPorUsuario(Sesion.getUsuarioId())
        );
        comprasTable.setItems(ventas);
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