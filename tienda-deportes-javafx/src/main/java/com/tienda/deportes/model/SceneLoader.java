package com.tienda.deportes.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneLoader {
    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error al cargar la escena: " + fxmlPath);
            e.printStackTrace();
            throw new RuntimeException("Error al cargar la escena: " + e.getMessage());
        }
    }
}