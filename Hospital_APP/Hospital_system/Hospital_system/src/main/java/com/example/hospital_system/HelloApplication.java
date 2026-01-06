package com.example.hospital_system;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        // Auto-fix database schema
        com.example.hospital_system.Database.DatabaseMigration.updateSchema();

        // On sauvegarde le Stage dans le SceneManager
        SceneManager.setStage(stage);

        // On charge la première scène (login)
        SceneManager.changeScene("/com/example/hospital_system/View/Dashboard.fxml");

        stage.setTitle("Hospital App");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
