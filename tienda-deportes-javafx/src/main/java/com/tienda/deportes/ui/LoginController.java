package com.tienda.deportes.ui;

import com.tienda.deportes.bd.UsuarioDAO;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {
    @FXML private TextField usuarioField;
    @FXML private PasswordField contrasenaField;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void iniciarSesion() {
        String usuario = usuarioField.getText().trim();
        String contrasena = contrasenaField.getText().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Debe completar todos los campos");
            // Efecto de shake en los campos vacíos
            shakeField(usuarioField);
            shakeField(contrasenaField);
            return;
        }

        try {
            if (usuarioDAO.validarUsuario(usuario, contrasena)) {
                int id = usuarioDAO.obtenerIdUsuario(usuario);
                String tipo = usuarioDAO.obtenerTipoUsuario(usuario);
                
                Sesion.iniciarSesion(id, usuario, "admin".equals(tipo));
                cargarMenu();
            } else {
                mostrarAlerta("Error", "Usuario o contraseña inválidos");
                shakeField(usuarioField);
                shakeField(contrasenaField);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Falló el inicio de sesión: " + e.getMessage());
        }
    }

    private void shakeField(Node node) {
        // Resetear la posición primero
        node.setTranslateX(0);
        
        // Crear la animación de shake
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), -10)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 10)),
            new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), -10)),
            new KeyFrame(Duration.millis(400), new KeyValue(node.translateXProperty(), 10)),
            new KeyFrame(Duration.millis(500), new KeyValue(node.translateXProperty(), 0))
        );
        timeline.setCycleCount(2); // Repetir el shake 2 veces
        timeline.play();
    }

    @FXML
    private void cargarRegistro() {
        try {
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            SceneLoader.loadScene(stage, "/com/tienda/deportes/ui/register.fxml", "Registro");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar el registro: " + e.getMessage());
        }
    }

    private void cargarMenu() {
        try {
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            SceneLoader.loadScene(stage, "/com/tienda/deportes/ui/menu.fxml", "Menú Principal");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar el menú: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}