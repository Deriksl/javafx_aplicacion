package com.tienda.deportes.ui;

import com.tienda.deportes.bd.UsuarioDAO;
import com.tienda.deportes.model.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarUsuarioController {
    @FXML private TextField nombreField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> tipoComboBox;
    @FXML private Button guardarBtn;
    @FXML private Button cancelarBtn;

    private Usuario usuario;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Configurar opciones del ComboBox
        tipoComboBox.getItems().addAll("admin", "cliente");
        
        // Configurar acciones de botones
        guardarBtn.setOnAction(e -> guardarCambios());
        cancelarBtn.setOnAction(e -> cerrarVentana());
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        nombreField.setText(usuario.getNombre());
        passwordField.setText(usuario.getPassword());
        tipoComboBox.setValue(usuario.getTipo());
    }

    private void guardarCambios() {
        if (validarCampos()) {
            usuario.setNombre(nombreField.getText().trim());
            usuario.setPassword(passwordField.getText().trim());
            usuario.setTipo(tipoComboBox.getValue());
            
            if (usuarioDAO.actualizarUsuario(usuario)) {
                mostrarAlerta("Éxito", "Usuario actualizado correctamente");
                cerrarVentana();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el usuario");
            }
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().trim().isEmpty() || 
            passwordField.getText().trim().isEmpty() || 
            tipoComboBox.getValue() == null) {
            
            mostrarAlerta("Error", "Todos los campos son obligatorios");
            return false;
        }
        return true;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) guardarBtn.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}