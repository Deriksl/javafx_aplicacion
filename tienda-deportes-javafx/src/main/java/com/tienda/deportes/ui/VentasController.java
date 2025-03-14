package com.tienda.deportes.ui;

import com.tienda.deportes.bl.ProductoBL;
import com.tienda.deportes.model.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class VentasController {

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> columnaNombre;

    @FXML
    private TableColumn<Producto, String> columnaDescripcion;

    @FXML
    private TableColumn<Producto, String> columnaMarca;

    @FXML
    private TableColumn<Producto, Integer> columnaStock;

    @FXML
    private TableColumn<Producto, Double> columnaPrecio;

    @FXML
    private Button btnAnadirCarrito;

    private ProductoBL productoBL;

    public VentasController() {
        productoBL = new ProductoBL();
    }

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Cargar los productos en la tabla
        tablaProductos.setItems(obtenerProductos());

        // Configurar el botón para añadir al carrito
        btnAnadirCarrito.setOnAction(e -> añadirAlCarrito());
    }

    private ObservableList<Producto> obtenerProductos() {
        return FXCollections.observableArrayList(productoBL.obtenerProductos());
    }

    private void añadirAlCarrito() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            // Lógica para añadir al carrito
            System.out.println("Añadiendo al carrito: " + productoSeleccionado.getNombre());
        } else {
            System.out.println("Selecciona un producto.");
        }
    }
}