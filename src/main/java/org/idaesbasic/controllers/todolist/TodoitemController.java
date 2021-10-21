package org.idaesbasic.controllers.todolist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TodoitemController {

    @FXML
    private CheckBox checkBox;
    
    @FXML
    private HBox panel;

    @FXML
    void deleteTodoAction(ActionEvent event) {
        ((VBox) panel.getParent()).getChildren().remove(panel);
    }

    void setTodo(String todo) {
        checkBox.setText(todo);
    }

    String getTodo() {
        return checkBox.getText();
    }

    Boolean isDone() {
        return checkBox.isSelected();
    }

    void setDone(Boolean value) {
        checkBox.setSelected(value);
    }
}
