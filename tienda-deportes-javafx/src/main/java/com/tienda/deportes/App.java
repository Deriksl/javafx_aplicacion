package com.tienda.deportes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/tienda/deportes/ui/login.fxml"));
        primaryStage.setTitle("Tienda de Deportes - Login");
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Pantalla completa al iniciar
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}