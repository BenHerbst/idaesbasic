package org.idaesbasic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Idaesbasic extends Application {

    @Override
    public void start(Stage stage) {
        try {
        	//Show welcome screen
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/WelcomeScreen.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Idaesbasic / Welcome - 0.9.0 - Beta");
            stage.setHeight(530);
            stage.setWidth(650);
            stage.setMaximized(false);
            stage.setResizable(false);
            //Close all timertasks on main window close
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            //Add icon
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}