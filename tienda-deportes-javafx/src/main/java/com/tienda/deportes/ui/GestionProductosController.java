package com.tienda.deportes.ui;

import java.io.File;

import com.tienda.deportes.bd.ProductoDAO;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GestionProductosController {
    @FXML private TableView<Producto> productosTable;
    @FXML private TableColumn<Producto, Integer> idColumn;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, String> descripcionColumn;
    @FXML private TableColumn<Producto, String> marcaColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TextField nombreField;
    @FXML private TextArea descripcionField;
    @FXML private TextField marcaField;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ImageView imagenView;
    @FXML private Button agregarBtn;
    @FXML private Button actualizarBtn;
    @FXML private Button eliminarBtn;
    @FXML private Button seleccionarImagenBtn;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private ObservableList<Producto> productosList;
    private Producto productoSeleccionado;
    private String imagenPath;

    @FXML
    public void initialize() {
        try {
            Sesion.verificarAdmin();
            inicializarComponentes();
        } catch (SecurityException e) {
            manejarErrorAcceso();
        } catch (Exception e) {
            manejarErrorGeneral("Error al inicializar: " + e.getMessage());
        }
    }

    private void inicializarComponentes() {
        configurarTabla();
        cargarProductos();
        configurarListeners();
        configurarBotones();
    }

    private void configurarTabla() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        marcaColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void cargarProductos() {
        productosList = FXCollections.observableArrayList(productoDAO.obtenerTodos());
        productosTable.setItems(productosList);
    }

    private void configurarListeners() {
        productosTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                productoSeleccionado = newSelection;
                if (productoSeleccionado != null) {
                    cargarDatosProducto();
                }
                actualizarEstadoBotones();
            });
    }

    private void configurarBotones() {
        actualizarBtn.setDisable(true);
        eliminarBtn.setDisable(true);
    }

    private void cargarDatosProducto() {
        nombreField.setText(productoSeleccionado.getNombre());
        descripcionField.setText(productoSeleccionado.getDescripcion());
        marcaField.setText(productoSeleccionado.getMarca());
        precioField.setText(String.valueOf(productoSeleccionado.getPrecio()));
        stockField.setText(String.valueOf(productoSeleccionado.getStock()));
        
        if (productoSeleccionado.getImagen() != null && !productoSeleccionado.getImagen().isEmpty()) {
            try {
                File file = new File(productoSeleccionado.getImagen());
                if (file.exists()) {
                    imagenView.setImage(new Image(file.toURI().toString()));
                    imagenPath = productoSeleccionado.getImagen();
                }
            } catch (Exception e) {
                imagenView.setImage(null);
            }
        } else {
            imagenView.setImage(null);
        }
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = productoSeleccionado != null;
        actualizarBtn.setDisable(!haySeleccion);
        eliminarBtn.setDisable(!haySeleccion);
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del producto");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(imagenView.getScene().getWindow());
        if (file != null) {
            imagenPath = file.getAbsolutePath();
            imagenView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void agregarProducto() {
        try {
            Producto producto = crearProductoDesdeCampos();
            if (producto != null) {
                producto.setImagen(imagenPath);
                if (productoDAO.agregarProducto(producto)) {
                    mostrarAlerta("Éxito", "Producto agregado correctamente");
                    limpiarCampos();
                    cargarProductos();
                } else {
                    mostrarAlerta("Error", "No se pudo agregar el producto");
                }
            }
        } catch (Exception e) {
            manejarErrorGeneral("Error al agregar producto: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarProducto() {
        try {
            if (productoSeleccionado != null) {
                Producto producto = crearProductoDesdeCampos();
                if (producto != null) {
                    producto.setId(productoSeleccionado.getId());
                    producto.setImagen(imagenPath != null ? imagenPath : productoSeleccionado.getImagen());
                    
                    if (productoDAO.actualizarProducto(producto)) {
                        mostrarAlerta("Éxito", "Producto actualizado correctamente");
                        cargarProductos();
                    } else {
                        mostrarAlerta("Error", "No se pudo actualizar el producto");
                    }
                }
            }
        } catch (Exception e) {
            manejarErrorGeneral("Error al actualizar producto: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        try {
            if (productoSeleccionado != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar eliminación");
                alert.setHeaderText(null);
                alert.setContentText("¿Estás seguro de eliminar el producto " + productoSeleccionado.getNombre() + "?");
                
                if (alert.showAndWait().get() == ButtonType.OK) {
                    if (productoDAO.eliminarProducto(productoSeleccionado.getId())) {
                        mostrarAlerta("Éxito", "Producto eliminado correctamente");
                        limpiarCampos();
                        cargarProductos();
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el producto");
                    }
                }
            }
        } catch (Exception e) {
            manejarErrorGeneral("Error al eliminar producto: " + e.getMessage());
        }
    }

    @FXML
    private void regresarAlMenu() {
        try {
            Stage stage = (Stage) productosTable.getScene().getWindow();
            SceneLoader.loadScene(stage, "/com/tienda/deportes/ui/menu.fxml", "Menú Principal");
        } catch (Exception e) {
            manejarErrorGeneral("Error al regresar al menú: " + e.getMessage());
        }
    }

    private Producto crearProductoDesdeCampos() {
        try {
            String nombre = nombreField.getText().trim();
            String descripcion = descripcionField.getText().trim();
            String marca = marcaField.getText().trim();
            double precio = Double.parseDouble(precioField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            
            if (nombre.isEmpty() || descripcion.isEmpty() || marca.isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios");
                return null;
            }
            
            return new Producto();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y stock deben ser valores numéricos válidos");
            return null;
        }
    }

    private void limpiarCampos() {
        nombreField.clear();
        descripcionField.clear();
        marcaField.clear();
        precioField.clear();
        stockField.clear();
        imagenView.setImage(null);
        imagenPath = null;
        productoSeleccionado = null;
        actualizarEstadoBotones();
    }

    private void manejarErrorAcceso() {
        mostrarAlerta("Acceso denegado", "No tienes permisos para acceder a esta función");
        regresarAlMenu();
    }

    private void manejarErrorGeneral(String mensaje) {
        mostrarAlerta("Error", mensaje);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}