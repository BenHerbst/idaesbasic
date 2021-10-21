package org.ainm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 640, 480);
            stage.setScene(scene);
            stage.setTitle("Idaesbasic - 0.8 - Alpha");
            stage.setMaximized(true);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}