package org.idaesbasic.controllers.kanban;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.idaesbasic.models.TaskRowModel;

public class TaskRowController implements Initializable {

    @FXML
    private VBox todoContainer;

    @FXML
    private Label title;

    TaskRowModel taskModel = new TaskRowModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        title.textProperty().bindBidirectional(taskModel.titleProperty());
    }    
}