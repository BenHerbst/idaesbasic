package org.idaesbasic.controllers.todolist;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CreateNewTodoController {
    @FXML
    private TextField todoField;

    @FXML
    private DatePicker datePicker;
    
    public String getTodo() {
        return todoField.getText();
    }
    
    public LocalDate getDate() {
        return datePicker.getValue();
    }
    
    @FXML
    void initialize() {
        datePicker.setValue(LocalDate.now());
    }
}