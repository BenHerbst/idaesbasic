package org.idaesbasic.controllers.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateNewTodoController {
    @FXML
    private TextField todoField;
    
    public String getTodo() {
        return todoField.getText();
    }
}