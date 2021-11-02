package org.idaesbasic.controllers.kanban;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.idaesbasic.controllers.todolist.TodoitemController;
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

    @FXML
    void addNewTask() throws IOException {
        FXMLLoader newTaskLoader = new FXMLLoader();
        newTaskLoader.setLocation(getClass().getResource("/fxml/dialogs/AddTodo.fxml"));
        DialogPane newTaskDialogPane = newTaskLoader.load();
        Dialog<ButtonType> newTaskDialog = new Dialog<ButtonType>();
        newTaskDialog.setDialogPane(newTaskDialogPane);
        Optional<ButtonType> result = newTaskDialog.showAndWait();
        if(result.get() == ButtonType.FINISH) {
            // Load a new todo item
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/views/todo/todo_item.fxml"));
            Node todo_item = loader.load();
            // Add todo to todolist
            todoContainer.getChildren().add(todo_item);
        }
    }
}