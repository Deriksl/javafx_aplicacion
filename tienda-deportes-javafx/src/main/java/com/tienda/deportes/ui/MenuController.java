package com.tienda.deportes.ui;

import java.io.IOException;
import java.util.List;

import com.tienda.deportes.bd.CarritoDAO;
import com.tienda.deportes.bd.ProductoDAO;
import com.tienda.deportes.model.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MenuController {
    @FXML
    private TableView<Producto> productosTable;
    @FXML
    private TableColumn<Producto, String> nombreColumn;
    @FXML
    private TableColumn<Producto, String> descripcionColumn;
    @FXML
    private TableColumn<Producto, String> marcaColumn;
    @FXML
    private TableColumn<Producto, Double> precioColumn;
    @FXML
    private TableColumn<Producto, Integer> stockColumn;
    @FXML
    private TableColumn<Producto, Void> agregarColumn;

    private ObservableList<Producto> productosObservableList;

    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        marcaColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Configurar la columna de "Agregar al carrito"
        agregarColumn.setCellFactory(param -> new TableCell<>() {
            private final Button agregarButton = new Button("Agregar al carrito");

            {
                agregarButton.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    agregarAlCarrito(producto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(agregarButton);
                }
            }
        });

        // Obtener los productos de la base de datos
        ProductoDAO productoDAO = new ProductoDAO();
        List<Producto> productos = productoDAO.obtenerTodos();

        // Convertir la lista a un ObservableList y asignarla a la tabla
        productosObservableList = FXCollections.observableArrayList(productos);
        productosTable.setItems(productosObservableList);
    }

    private void agregarAlCarrito(Producto producto) {
        CarritoDAO carritoDAO = new CarritoDAO();
        carritoDAO.agregarProductoAlCarrito(1, producto.getId(), 1); // Suponiendo que el usuario tiene id=1
    }

    @FXML
    private void mostrarCarrito() {
        try {
            // Cargar la ventana del carrito
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/carrito.fxml"));
            Parent root = loader.load();
    
            // Obtener la ventana actual (Stage)
            Stage stage = (Stage) productosTable.getScene().getWindow();
    
            // Reemplazar la escena actual con la escena del carrito
            stage.setScene(new Scene(root));
            stage.setTitle("Carrito de Compras");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            // Cargar la ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void verCompras(ActionEvent event) {
        try {
            // Cargar la ventana de compras
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/compra.fxml"));
            Parent root = loader.load();
    
            // Obtener la ventana actual
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    
            // Cambiar la escena de la ventana actual
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
