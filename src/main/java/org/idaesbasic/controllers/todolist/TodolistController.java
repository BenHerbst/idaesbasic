package org.idaesbasic.controllers.todolist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.idaesbasic.models.ViewModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TodolistController {

    public ViewModel viewModel = new ViewModel();

    @FXML
    private VBox todos_list;

    @FXML
    private AnchorPane pane;

    @FXML
    void add_todo_item(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/dialogs/AddTodo.fxml"));
        DialogPane addTodoDialog = loader.load();
        CreateNewTodoController controller = loader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(addTodoDialog);
        dialog.setTitle("Add new todo");
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.FINISH) {
            addTodo(controller.getTodo(), false, controller.getDate());
        }
    }

    void addTodo(String todo, Boolean checked, LocalDate date) throws IOException {
        // Load a new todo item
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/views/todo/TodoItem.fxml"));
        Node todo_item = loader.load();
        //Set todo text
        TodoitemController todoController = loader.getController();
        todo_item.setUserData(todoController);
        todoController.setTodo(todo);
        todoController.setDone(checked);
        todoController.setDate(date);
        // Add todo to todolist
        todos_list.getChildren().add(todo_item);
    }

    public void loadFile(Path file) {
        viewModel.setOpenedFile(file);
        // Add each line from file to todolist
        try (Stream<String> lines = Files.lines(file)) {
            lines.forEach((line) -> {
                try {
                    //Date detect pattern, to get the date of a todo item
                    Pattern datePattern = Pattern.compile("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[0-1])");
                    Matcher matcher = datePattern.matcher(line);
                    LocalDate date = matcher.find() ? LocalDate.parse(matcher.group(0)) : null;
                    if (date != null) {
                        line = line.replace(date.toString(), "");
                    }
                    addTodo(line.replace("[x] ", "").replace("[ ] ", ""), (line.startsWith("[x] ")), date);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFileAs(Path file) {
        viewModel.setOpenedFile(file);
        try {
            saveFile(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveCurrentFile() throws IOException {
        saveFile(viewModel.getOpenedFile());
    }

    void saveFile(Path file) throws IOException {
        //Create a string with a todo per line
        String saveData = "";
        for (Node todoItem : todos_list.getChildren()) {
            TodoitemController controller = (TodoitemController) getController(todoItem);
            saveData += "[" + ((controller.isDone()) ? "x" : " ") + "] " + controller.getTodo() + " " + controller.getDateAsString() + "\n";
        }
        //Save this string to the given file
        Files.writeString(file, saveData);
    }

    @FXML
    void deleteCurrentFile() throws IOException {
        viewModel.deleteCurrentFile();
        // Remove the todo panel
        TabPane tabPane = ((TabPane) pane.getParent().getParent());
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.getSelectedItem().setContent(null);
    }

    public static Object getController(Node node) {
        Object controller = null;
        do {
            controller = node.getUserData();
            node = node.getParent();
        } while (controller == null && node != null);
        return controller;
    }
}
