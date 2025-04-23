package com.tienda.deportes.ui;

import com.tienda.deportes.bd.UsuarioDAO;
import com.tienda.deportes.model.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private TextField usuarioField;

    @FXML
    private PasswordField contraseñaField;

    @FXML
    private void registrarUsuario() {
        String usuario = usuarioField.getText().trim();
        String contraseña = contraseñaField.getText().trim();

        if (usuario.isEmpty() || contraseña.isEmpty()) {
            mostrarError("Todos los campos son obligatorios");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(usuario);
        nuevoUsuario.setPassword(contraseña);
        nuevoUsuario.setTipo("cliente"); // Por defecto todos los nuevos usuarios son clientes

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (usuarioDAO.registrarUsuario(nuevoUsuario)) {
            mostrarMensaje("Registro exitoso");
            volverAlLogin();
        } else {
            mostrarError("Error al registrar usuario. ¿El nombre de usuario ya existe?");
        }
    }

    @FXML
    private void volverAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();
        } catch (Exception e) {
            mostrarError("Error al regresar al login: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}