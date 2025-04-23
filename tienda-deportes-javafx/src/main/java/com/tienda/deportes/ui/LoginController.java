package com.tienda.deportes.ui;

import com.tienda.deportes.bd.UsuarioDAO;
import com.tienda.deportes.model.SceneLoader;
import com.tienda.deportes.model.Sesion;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usuarioField;
    @FXML private PasswordField contrasenaField;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void iniciarSesion() {
        String usuario = usuarioField.getText().trim();
        String contrasena = contrasenaField.getText().trim();

        // Debug detallado
        System.out.println("\n=== Intento de Login ===");
        System.out.println("Usuario: " + usuario);
        System.out.println("Contraseña: " + contrasena);

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Debe completar todos los campos");
            return;
        }

        try {
            System.out.println("Validando con BD...");
            if (usuarioDAO.validarUsuario(usuario, contrasena)) {
                int id = usuarioDAO.obtenerIdUsuario(usuario);
                String tipo = usuarioDAO.obtenerTipoUsuario(usuario);
                
                System.out.println("Autenticación exitosa:");
                System.out.println("ID: " + id + " | Tipo: " + tipo);
                
                Sesion.iniciarSesion(id, usuario, "admin".equals(tipo));
                cargarMenu();
            } else {
                System.out.println("Credenciales incorrectas");
                mostrarAlerta("Error", "Usuario o contraseña inválidos");
            }
        } catch (Exception e) {
            System.err.println("Error crítico en login:");
            e.printStackTrace();
            mostrarAlerta("Error", "Falló el inicio de sesión: " + e.getMessage());
        }
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