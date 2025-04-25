package com.tienda.deportes.ui;

import java.io.File;
import java.io.InputStream;

import com.tienda.deportes.bd.CarritoDAO;
import com.tienda.deportes.bd.ProductoDAO;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuController {
    @FXML private TableView<Producto> productosTable;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;
    @FXML private TableColumn<Producto, String> imagenColumn;
    @FXML private TableColumn<Producto, Void> accionesColumn;
    @FXML private VBox menuButtonsContainer; // Cambiado de HBox a VBox
    @FXML private Button carritoBtn;
    @FXML private Button comprasBtn;
    @FXML private Button gestionUsuariosBtn;
    @FXML private Button gestionProductosBtn;
    @FXML private Button reporteVentasBtn;
    @FXML private Button reporteInventarioBtn;
    @FXML private Button cerrarSesionBtn;
    @FXML private Label usuarioLabel;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CarritoDAO carritoDAO = new CarritoDAO();
    private ObservableList<Producto> productosList;

    @FXML
    public void initialize() {
        if (!validarSesion()) return;
        
        usuarioLabel.setText("Bienvenido, " + Sesion.getNombreUsuario());
        configurarTabla();
        cargarProductos();
        configurarMenuSegunRol();
        configurarAccionesBotones();
    }

    private void configurarTabla() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        precioColumn.setCellFactory(column -> new TableCell<Producto, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                    setStyle("-fx-alignment: CENTER-RIGHT;");
                }
            }
        });
        
        imagenColumn.setCellValueFactory(cellData -> {
            String imagePath = cellData.getValue().getImagen();
            return new javafx.beans.property.SimpleStringProperty(imagePath);
        });
        
        imagenColumn.setCellFactory(column -> new TableCell<Producto, String>() {
            private final ImageView imageView = new ImageView();
            
            {
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.setPreserveRatio(true);
            }
            
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        InputStream inputStream = getClass().getResourceAsStream(imagePath);
                        if (inputStream != null) {
                            Image image = new Image(inputStream);
                            imageView.setImage(image);
                            setGraphic(imageView);
                        } else {
                            File file = new File(imagePath);
                            if (file.exists()) {
                                Image image = new Image(file.toURI().toString());
                                imageView.setImage(image);
                                setGraphic(imageView);
                            } else {
                                System.err.println("Archivo de imagen no encontrado: " + imagePath);
                                setGraphic(null);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error al cargar imagen: " + imagePath);
                        setGraphic(null);
                    }
                }
            }
        });
        
        accionesColumn.setCellFactory(param -> new TableCell<>() {
            private final Button agregarBtn = new Button("🛒 Agregar");
            
            {
                agregarBtn.getStyleClass().add("action-button");
                agregarBtn.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    if (carritoDAO.agregarProductoAlCarrito(
                        Sesion.getUsuarioId(), 
                        producto.getId(), 
                        1
                    )) {
                        mostrarAlerta("Éxito", "Producto agregado al carrito", Alert.AlertType.INFORMATION);
                    } else {
                        mostrarAlerta("Error", "No se pudo agregar al carrito", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(agregarBtn);
                }
            }
        });
    }

    private void cargarProductos() {
        productosList = FXCollections.observableArrayList(productoDAO.obtenerTodos());
        productosTable.setItems(productosList);
    }

    private boolean validarSesion() {
        if (Sesion.getNombreUsuario() == null) {
            cargarVentana("/com/tienda/deportes/ui/login.fxml", "Iniciar Sesión");
            return false;
        }
        return true;
    }

    private void configurarMenuSegunRol() {
        menuButtonsContainer.getChildren().clear();
        menuButtonsContainer.getChildren().addAll(carritoBtn, comprasBtn);

        if (Sesion.esAdmin()) {
            gestionUsuariosBtn.setVisible(true);
            gestionProductosBtn.setVisible(true);
            reporteVentasBtn.setVisible(true);
            reporteInventarioBtn.setVisible(true);
            
            menuButtonsContainer.getChildren().addAll(gestionUsuariosBtn, gestionProductosBtn, reporteVentasBtn, reporteInventarioBtn);
        } else {
            gestionUsuariosBtn.setVisible(false);
            gestionProductosBtn.setVisible(false);
            reporteVentasBtn.setVisible(false);
            reporteInventarioBtn.setVisible(false);
        }

        menuButtonsContainer.getChildren().add(cerrarSesionBtn);
    }

    private void configurarAccionesBotones() {
        carritoBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/carrito.fxml", "Carrito"));
        comprasBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/compras.fxml", "Mis Compras"));
        gestionUsuariosBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/gestion_usuarios.fxml", "Gestión Usuarios"));
        gestionProductosBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/gestion_productos.fxml", "Gestión Productos"));
        reporteVentasBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/reporte_ventas.fxml", "Reporte de Ventas"));
        reporteInventarioBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/reporte_inventario.fxml", "Reporte de Inventario"));
        cerrarSesionBtn.setOnAction(e -> cerrarSesion());
    }

    private void cargarVentana(String fxml, String titulo) {
        try {
            Stage stage = new Stage();
            SceneLoader.loadScene(stage, fxml, titulo);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar la ventana: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cerrarSesion() {
        Sesion.cerrarSesion();
        cargarVentana("/com/tienda/deportes/ui/login.fxml", "Iniciar Sesión");
        ((Stage) cerrarSesionBtn.getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}