package com.tienda.deportes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML desde el classpath
        Parent root = FXMLLoader.load(getClass().getResource("/com/tienda/deportes/ui/login.fxml"));
        primaryStage.setTitle("Tienda de Deportes - Login");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}