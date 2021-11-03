package org.idaesbasic.controllers.todolist;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TodoitemController {

    @FXML
    private CheckBox checkBox;
    
    @FXML
    private HBox panel;

    @FXML
    private Label dateLabel;

    @FXML
    void deleteTodoAction(ActionEvent event) {
        ((VBox) panel.getParent()).getChildren().remove(panel);
    }

    public void setTodo(String todo) {
        checkBox.setText(todo);
    }

    public void setDate(LocalDate date) {
        if (date != null) {
            dateLabel.setText(date.toString());
        }
    }

    String getDateAsString() {
        return dateLabel.getText();
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
