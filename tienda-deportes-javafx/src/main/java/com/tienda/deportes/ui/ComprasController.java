package com.tienda.deportes.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ComprasController {
    @FXML private TableView<?> comprasTable;

    @FXML
    private void regresarAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tienda/deportes/ui/menu.fxml"));
            Parent root = loader.load();
            
            // No necesitamos pasar el tipoUsuario ya que MenuController usa Sesion.esAdmin()
            
            Stage stage = (Stage) comprasTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}