package com.tienda.deportes.ui;

import java.util.ArrayList;
import java.util.List;

import com.tienda.deportes.bd.CarritoDAO;
import com.tienda.deportes.bd.VentaDAO;
import com.tienda.deportes.model.DetalleVenta;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.Venta;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CarritoController {
    @FXML
    private TableView<Producto> carritoTable;
    @FXML
    private TableColumn<Producto, String> nombreColumn;
    @FXML
    private TableColumn<Producto, Double> precioColumn;
    @FXML
    private TableColumn<Producto, Void> eliminarColumn;
    @FXML
    private Label precioTotalLabel;

    private ObservableList<Producto> carritoObservableList;
    private CarritoDAO carritoDAO = new CarritoDAO();
    private VentaDAO ventaDAO = new VentaDAO();

    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Configurar la columna de "Eliminar"
        eliminarColumn.setCellFactory(param -> new TableCell<>() {
            private final Button eliminarButton = new Button("Eliminar");

            {
                eliminarButton.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    eliminarDelCarrito(producto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(eliminarButton);
                }
            }
        });

        // Cargar los productos del carrito
        cargarCarrito();
    }

    private void cargarCarrito() {
        List<Producto> productos = carritoDAO.obtenerProductosDelCarrito(1); // Suponiendo que el usuario tiene id=1
        carritoObservableList = FXCollections.observableArrayList(productos);
        carritoTable.setItems(carritoObservableList);

        // Calcular el precio total
        double precioTotal = productos.stream().mapToDouble(Producto::getPrecio).sum();
        precioTotalLabel.setText(String.format("Precio total: $%.2f", precioTotal));
    }

    private void eliminarDelCarrito(Producto producto) {
        carritoDAO.eliminarProductoDelCarrito(1, producto.getId()); // Suponiendo que el usuario tiene id=1
        carritoObservableList.remove(producto);

        // Recalcular el precio total
        double precioTotal = carritoObservableList.stream().mapToDouble(Producto::getPrecio).sum();
        precioTotalLabel.setText(String.format("Precio total: $%.2f", precioTotal));
    }

    @FXML
    private void comprarCarrito() {
        List<Producto> productos = carritoObservableList;

        // Crear la venta
        Venta venta = new Venta();
        venta.setUsuarioId(1); // Suponiendo que el usuario tiene id=1
        venta.setTotal(productos.stream().mapToDouble(Producto::getPrecio).sum());

        // Crear los detalles de la venta
        List<DetalleVenta> detalles = new ArrayList<>();
        for (Producto producto : productos) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(producto.getId());
            detalle.setCantidad(1); // Suponiendo que la cantidad es 1
            detalle.setPrecioUnitario(producto.getPrecio());
            detalles.add(detalle);
        }

        // Realizar la venta
        ventaDAO.realizarVenta(venta, detalles);

        // Limpiar el carrito
        carritoDAO.limpiarCarrito(1); // Suponiendo que el usuario tiene id=1
        cargarCarrito();
    }

@FXML
private void regresarAlMenu() {
    try {
        // Cargar la ventana del menú
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/menu.fxml"));
        Parent root = loader.load();

        // Obtener la ventana actual (Stage)
        Stage stage = (Stage) carritoTable.getScene().getWindow();

        // Reemplazar la escena actual con la escena del menú
        stage.setScene(new Scene(root));
        stage.setTitle("Menú Principal");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}