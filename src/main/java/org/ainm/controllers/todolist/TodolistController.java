package org.ainm.controllers.todolist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TodolistController {

    @FXML
    private VBox todos_list;

    @FXML
    void add_todo_item(ActionEvent event) throws IOException {
        addTodo("New todo");
    }

    void addTodo(String todo) throws IOException {
        // Load a new todo item
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/views/todo/todo_item.fxml"));
        Node todo_item = loader.load();
        //Set todo text
        TodoitemController todoController = loader.getController();
        todoController.setTodo(todo);
        // Add todo to todolist
        todos_list.getChildren().add(todo_item);
    }

    public void loadFile(Path file) {
        // Add each line from file to todolist
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach((line) -> {
                try {
                    addTodo(line);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileAs(Path file) throws IOException {
        //Create a string with a todo per line
        String saveData = "";
        for(Node todoItem:todos_list.getChildren()) {
            TodoitemController controller = (TodoitemController) getController(todoItem);
            saveData += controller.getTodo() + "\n";
        }
        //Save this string to the given file
        Files.writeString(file, saveData);
    }

    public static Object getController(Node node) {
        Object controller = null;
        do {
            controller = node.getProperties().get("foo");
            node = node.getParent();
        } while (controller == null && node != null);
        return controller;
    }
}