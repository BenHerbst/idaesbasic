package org.idaesbasic.controllers.kanban;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.idaesbasic.models.KanbanModel;

public class KanbanController implements Initializable {

    @FXML
    private HBox boardsContainer;

    KanbanModel kanbanModel = new KanbanModel();

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

    public void createTaskRowWithTitle(String title) throws IOException {
        // Create task row
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/views/kanban/TaskRow.fxml"));
        Node todoTaskRow = loader.load();
        TaskRowController controller = loader.getController();
        controller.taskModel.setTitle(title);
        // Add task row to container
        boardsContainer.getChildren().add(todoTaskRow);
    }
}
