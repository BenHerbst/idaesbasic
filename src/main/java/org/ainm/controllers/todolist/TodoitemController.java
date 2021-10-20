package org.ainm.controllers.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class TodoitemController {

    @FXML
    private CheckBox checkBox;

    void setTodo(String todo) {
        checkBox.setText(todo);
    }

    String getTodo() {
        return checkBox.getText();
    }

    Boolean isDone() {
        return checkBox.isChecked()
    }
}
