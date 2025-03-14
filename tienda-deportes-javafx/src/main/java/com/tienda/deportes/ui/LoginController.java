package com.tienda.deportes.ui;

import com.tienda.deportes.bd.UsuarioDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usuarioField;

    @FXML
    private PasswordField contraseñaField;

    @FXML
    private void iniciarSesion() {
        String usuario = usuarioField.getText();
        String contraseña = contraseñaField.getText();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (usuarioDAO.validarUsuario(usuario, contraseña)) {
            abrirMenu();
        } else {
            mostrarError("Usuario o contraseña incorrectos");
        }
    }

    @FXML
    private void abrirRegistro() {
        try {
            // Cargar la ventana de registro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void volver() {
        try {
            // Cargar la ventana de login (o cualquier otra ventana que desees)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirMenu() {
        try {
            // Cargar la ventana del menú
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}