package com.tienda.deportes.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneLoader {
    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SceneLoader.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error al cargar la escena: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al cargar la escena: " + e.getMessage(), e);
        }
    }
}