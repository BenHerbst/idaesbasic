package org.idaesbasic.controllers;

import java.io.IOException;
import java.nio.file.Paths;

import org.idaesbasic.data.Load;
import org.idaesbasic.models.Projects;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class WelcomeScreenController {
    
    Projects registeredProjects = new Projects();

    @FXML
    private ChoiceBox projectListBox;

    @FXML
    void addProject(ActionEvent event) throws IOException {
        // Load main scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        Parent parent = loader.load();
        Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        Scene scene = new Scene(parent);
        // Change stage
        thisStage.hide();
        thisStage.setMaximized(true);
        thisStage.setResizable(true);
        thisStage.setTitle("Idaesbasic / Main - 0.9.0 - Beta");
        thisStage.setScene(scene);
        thisStage.show();
        // Show open project window
        MainController controller = loader.getController();
        DirectoryChooser chooser = new DirectoryChooser();
        controller.addNewProject(chooser.showDialog(null));
    }

    @FXML
    void createNewProject(ActionEvent event) {

    }

    @FXML
    public void initialize() throws IOException {
        registeredProjects.loadProjectListFromUserFiles();
        projectListBox.setItems(FXCollections.observableArrayList(registeredProjects.getProjectList()));
        projectListBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            public void changed(ObservableValue ov, Object value, Object newValue) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
                Parent parent = null;
                try {
                    parent = loader.load();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Node node = (Node) projectListBox;
                Stage thisStage = (Stage) node.getScene().getWindow();
                Scene scene = new Scene(parent);
                // Change stage
                thisStage.hide();
                thisStage.setMaximized(true);
                thisStage.setResizable(true);
                thisStage.setTitle("Idaesbasic / Main - 0.9.0 - Beta");
                thisStage.setScene(scene);
                MainController controller = loader.getController();
                controller.openRegisteredProject(newValue.toString());
                thisStage.show();
            }
        });
    }
}