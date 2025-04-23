package com.tienda.deportes.ui;

import com.tienda.deportes.bd.CarritoDAO;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.Sesion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CarritoController {
    @FXML private TableView<Producto> carritoTable;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;
    @FXML private TableColumn<Producto, Integer> cantidadColumn;
    @FXML private Label totalLabel;
    @FXML private ImageView imagenView;

    private final CarritoDAO carritoDAO = new CarritoDAO();
    private ObservableList<Producto> productosCarrito;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCarrito();
    }

    private void configurarTabla() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidadEnCarrito"));

        carritoTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> mostrarImagenProducto(newVal));
    }

    private void cargarCarrito() {
        productosCarrito = FXCollections.observableArrayList(
            carritoDAO.obtenerProductosDelCarrito(Sesion.getUsuarioId())
        );
        carritoTable.setItems(productosCarrito);
        calcularTotal();
    }

    private void mostrarImagenProducto(Producto producto) {
        if (producto != null && producto.getImagen() != null) {
            try {
                Image image = new Image("file:" + producto.getImagen());
                imagenView.setImage(image);
            } catch (Exception e) {
                imagenView.setImage(null);
            }
        } else {
            imagenView.setImage(null);
        }
    }

    private void calcularTotal() {
        double total = productosCarrito.stream()
            .mapToDouble(p -> p.getPrecio() * p.getCantidadEnCarrito())
            .sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void aumentarCantidad() {
        Producto seleccionado = carritoTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            carritoDAO.actualizarCantidadProducto(
                Sesion.getUsuarioId(), 
                seleccionado.getId(), 
                seleccionado.getCantidadEnCarrito() + 1
            );
            cargarCarrito();
        }
    }

    @FXML
    private void disminuirCantidad() {
        Producto seleccionado = carritoTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null && seleccionado.getCantidadEnCarrito() > 1) {
            carritoDAO.actualizarCantidadProducto(
                Sesion.getUsuarioId(), 
                seleccionado.getId(), 
                seleccionado.getCantidadEnCarrito() - 1
            );
            cargarCarrito();
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado = carritoTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            carritoDAO.eliminarProductoDelCarrito(
                Sesion.getUsuarioId(), 
                seleccionado.getId()
            );
            cargarCarrito();
        }
    }

    @FXML
    private void realizarCompra() {
        // Implementación de la compra
        carritoDAO.limpiarCarrito(Sesion.getUsuarioId());
        cargarCarrito();
        mostrarAlerta("Compra realizada", "Su compra se ha realizado con éxito", Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}