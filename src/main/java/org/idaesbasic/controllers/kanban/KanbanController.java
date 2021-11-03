package org.idaesbasic.controllers.kanban;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.idaesbasic.models.KanbanModel;

public class KanbanController implements Initializable {

    @FXML
    private HBox boardsContainer;

    @FXML
    KanbanModel kanbanModel = new KanbanModel();

    @FXML
    TextField addRowTextField = new TextField();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Create the standard task rows
        for(String rowTitle:kanbanModel.getStandardTaskRows()) {
            try {
                createTaskRowWithTitle(rowTitle);
            } catch (IOException ex) {
                Logger.getLogger(KanbanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void addNewRowAction () throws IOException {
        createTaskRowWithTitle(addRowTextField.getText());
        addRowTextField.setText("");
    }

    public void createTaskRowWithTitle(String title) throws IOException {
        // Create task row
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/views/kanban/TaskRow.fxml"));
        Node todoTaskRow = loader.load();
        TaskRowController controller = loader.getController();
        assert controller.taskModel != null;
        controller.taskModel.setTitle(title);
        // Add task row to container
        boardsContainer.getChildren().add(todoTaskRow);
    }

    @FXML
    void textFieldKeyPressed(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER) {
            addNewRowAction();
        }
    }
}