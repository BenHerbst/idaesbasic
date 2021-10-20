package org.ainm.controllers.todolist;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class TodolistController {

    @FXML
    private VBox todos_list;

    @FXML
    void add_todo_item(ActionEvent event) throws IOException {
        // Add a new todo to the todos list
        Node todo_item = FXMLLoader.load(getClass().getResource("/fxml/views/todo/todo_item.fxml"));
        todos_list.getChildren().add(todo_item);
    }
}