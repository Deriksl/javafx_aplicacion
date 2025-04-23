package com.tienda.deportes.ui;

import com.tienda.deportes.bd.CarritoDAO;
import com.tienda.deportes.bd.ProductoDAO;
import com.tienda.deportes.model.Producto;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MenuController {
    @FXML private TableView<Producto> productosTable;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;
    @FXML private TableColumn<Producto, String> imagenColumn;
    @FXML private TableColumn<Producto, Void> accionesColumn;
    @FXML private HBox menuButtonsContainer;
    @FXML private Button carritoBtn;
    @FXML private Button comprasBtn;
    @FXML private Button reporteVentasBtn;
    @FXML private Button reporteInventarioBtn;
    @FXML private Button cerrarSesionBtn;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CarritoDAO carritoDAO = new CarritoDAO();
    private ObservableList<Producto> productosList;

    @FXML
    public void initialize() {
        if (!validarSesion()) return;
        
        configurarTabla();
        cargarProductos();
        configurarMenuSegunRol();
        configurarAccionesBotones();
    }

    private void configurarTabla() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        
        imagenColumn.setCellFactory(param -> new TableCell<Producto, String>() {
            private final ImageView imageView = new ImageView();
            
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }
            
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        System.err.println("Error al cargar imagen: " + e.getMessage());
                        setGraphic(null);
                    }
                }
            }
        });
        
        accionesColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Producto, Void> call(TableColumn<Producto, Void> param) {
                return new TableCell<>() {
                    private final Button agregarBtn = new Button("Agregar al carrito");
                    
                    {
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
                };
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
            Button gestionUsuariosBtn = new Button("Gestión Usuarios");
            Button gestionProductosBtn = new Button("Gestión Productos");
            reporteVentasBtn.setVisible(true);
            reporteInventarioBtn.setVisible(true);
            
            gestionUsuariosBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/gestion_usuarios.fxml", "Gestión Usuarios"));
            gestionProductosBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/gestion_productos.fxml", "Gestión Productos"));
            
            menuButtonsContainer.getChildren().addAll(gestionUsuariosBtn, gestionProductosBtn, reporteVentasBtn, reporteInventarioBtn);
        } else {
            reporteVentasBtn.setVisible(false);
            reporteInventarioBtn.setVisible(false);
        }

        menuButtonsContainer.getChildren().add(cerrarSesionBtn);
    }

    private void configurarAccionesBotones() {
        carritoBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/carrito.fxml", "Carrito"));
        comprasBtn.setOnAction(e -> cargarVentana("/com/tienda/deportes/ui/compras.fxml", "Mis Compras"));
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