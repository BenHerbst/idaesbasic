package org.idaesbasic.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WelcomeScreenController {

    @FXML
    void addProject(ActionEvent event) throws IOException {
    	//Load main scene
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
    	Parent parent = loader.load();
    	Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        Scene scene = new Scene(parent);
        //Change stage
        thisStage.hide();
        thisStage.setMaximized(true);
        thisStage.setResizable(true);
        thisStage.setTitle("Idaesbasic / Main - 0.9.0 - Beta");
        thisStage.setScene(scene);
        thisStage.show();
        //Show open project window
        MainController controller = loader.getController();
        controller.addNewProject();
    }

    @FXML
    void createNewProject(ActionEvent event) {

    }

}
