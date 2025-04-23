package com.tienda.deportes.ui;

import java.util.List;

import com.tienda.deportes.bd.UsuarioDAO;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;
import com.tienda.deportes.model.Usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GestionUsuariosController {
    @FXML private TableView<Usuario> usuariosTable;
    @FXML private TableColumn<Usuario, Integer> idColumn;
    @FXML private TableColumn<Usuario, String> nombreColumn;
    @FXML private TableColumn<Usuario, String> tipoColumn;
    @FXML private TableColumn<Usuario, String> passwordColumn;
    @FXML private Button editarBtn;
    @FXML private Button eliminarBtn;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        if (!Sesion.esAdmin()) {
            mostrarAlertaYRegresar();
            return;
        }
        
        configurarTabla();
        cargarUsuarios();
        configurarSeleccion();
    }

    private void configurarTabla() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodosUsuarios();
        ObservableList<Usuario> usuariosList = FXCollections.observableArrayList(usuarios);
        usuariosTable.setItems(usuariosList);
    }

    private void configurarSeleccion() {
        usuariosTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean seleccionado = newSelection != null;
                editarBtn.setDisable(!seleccionado);
                eliminarBtn.setDisable(!seleccionado);
            });
    }

    @FXML
    private void agregarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/registro_usuario.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Nuevo Usuario");
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro: " + e.getMessage());
        }
    }

    @FXML
    private void editarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/editar_usuario.fxml"));
                Parent root = loader.load();
                EditarUsuarioController controller = loader.getController();
                controller.setUsuario(seleccionado);
                
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Editar Usuario");
                stage.showAndWait();
                
                cargarUsuarios();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo abrir la ventana de edición: " + e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro de eliminar al usuario " + seleccionado.getNombre() + "?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                if (usuarioDAO.eliminarUsuario(seleccionado.getId())) {
                    mostrarAlerta("Éxito", "Usuario eliminado correctamente");
                    cargarUsuarios();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el usuario");
                }
            }
        }
    }

    @FXML
    private void regresarAlMenu() {
        try {
            Stage stage = (Stage) usuariosTable.getScene().getWindow();
            SceneLoader.loadScene(stage, "/com/tienda/deportes/ui/menu.fxml", "Menú Principal");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo regresar al menú: " + e.getMessage());
        }
    }

    private void mostrarAlertaYRegresar() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Acceso denegado");
        alert.setHeaderText(null);
        alert.setContentText("No tienes permisos para acceder a esta función");
        alert.showAndWait();
        regresarAlMenu();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}