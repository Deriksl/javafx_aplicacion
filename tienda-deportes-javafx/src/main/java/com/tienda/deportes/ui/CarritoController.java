package com.tienda.deportes.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.tienda.deportes.bd.CarritoDAO;
import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.DetalleVenta;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;
import com.tienda.deportes.model.Venta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
            if(seleccionado.getCantidadEnCarrito() < seleccionado.getStock()) {
                boolean exito = carritoDAO.actualizarCantidadProducto(
                    Sesion.getUsuarioId(), 
                    seleccionado.getId(), 
                    seleccionado.getCantidadEnCarrito() + 1
                );
                if(exito) {
                    cargarCarrito();
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar la cantidad", Alert.AlertType.ERROR);
                }
            } else {
                mostrarAlerta("Stock insuficiente", "No hay suficiente stock disponible", Alert.AlertType.WARNING);
            }
        } else {
            mostrarAlerta("Selección requerida", "Por favor seleccione un producto", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void disminuirCantidad() {
        Producto seleccionado = carritoTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (seleccionado.getCantidadEnCarrito() > 1) {
                boolean exito = carritoDAO.actualizarCantidadProducto(
                    Sesion.getUsuarioId(), 
                    seleccionado.getId(), 
                    seleccionado.getCantidadEnCarrito() - 1
                );
                if(exito) {
                    cargarCarrito();
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar la cantidad", Alert.AlertType.ERROR);
                }
            } else {
                eliminarProducto();
            }
        } else {
            mostrarAlerta("Selección requerida", "Por favor seleccione un producto", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado = carritoTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            boolean exito = carritoDAO.eliminarProductoDelCarrito(
                Sesion.getUsuarioId(), 
                seleccionado.getId()
            );
            if(exito) {
                cargarCarrito();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el producto", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección requerida", "Por favor seleccione un producto", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void realizarCompra() {
        if(productosCarrito.isEmpty()) {
            mostrarAlerta("Carrito vacío", "No hay productos en el carrito", Alert.AlertType.WARNING);
            return;
        }

        // Crear la venta
        Venta venta = new Venta();
        venta.setUsuarioId(Sesion.getUsuarioId());
        venta.setTotal(productosCarrito.stream()
            .mapToDouble(p -> p.getPrecio() * p.getCantidadEnCarrito())
            .sum());

        // Crear detalles de venta
        List<DetalleVenta> detalles = productosCarrito.stream()
            .map(p -> {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProductoId(p.getId());
                detalle.setCantidad(p.getCantidadEnCarrito());
                detalle.setPrecioUnitario(p.getPrecio());
                return detalle;
            })
            .collect(Collectors.toList());

        // Registrar la venta
        VentaDAO ventaDAO = new VentaDAO();
        ventaDAO.realizarVenta(venta, detalles);

        // Limpiar carrito
        carritoDAO.limpiarCarrito(Sesion.getUsuarioId());
        cargarCarrito();
        
        mostrarAlerta("Compra realizada", "Su compra se ha registrado con éxito", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void regresarAlMenu(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene(stage, "Menú Principal", "/com/tienda/deportes/ui/menu.fxml");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}