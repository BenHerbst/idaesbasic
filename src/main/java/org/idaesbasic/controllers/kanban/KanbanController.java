package org.idaesbasic.controllers.kanban;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.fortuna.ical4j.validate.ValidationException;
import org.idaesbasic.controllers.todolist.TodoitemController;
import org.idaesbasic.models.KanbanModel;
import org.idaesbasic.models.ViewModel;
import org.idaesbasic.models.kanban.kanbanTaskModel;
import org.idaesbasic.models.kanban.taskRowModel;

public class KanbanController implements Initializable {

    public ViewModel viewModel = new ViewModel();

    @FXML
    private HBox boardsContainer;

    @FXML
    KanbanModel kanbanModel = new KanbanModel();

    @FXML
    TextField addRowTextField = new TextField();

    @FXML
    AnchorPane pane = new AnchorPane();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        // Create the standard task rows
        for(taskRowModel row:kanbanModel.getStandardTaskRows()) {
            try {
                createTaskRowWithTitle(row.getName());
            } catch (IOException ex) {
                Logger.getLogger(KanbanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void addNewRowAction () throws IOException {
        createTaskRowWithTitle(addRowTextField.getText());
        addRowTextField.setText("");
    }

    public void createTaskRowWithTitle(String title) throws IOException {
        // Create task row
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/views/kanban/TaskRow.fxml"));
        Node todoTaskRow = loader.load();
        TaskRowController controller = loader.getController();
        assert controller.taskModel != null;
        controller.taskModel.setName(title);
        todoTaskRow.setUserData(controller);
        // Add task row to container
        boardsContainer.getChildren().add(todoTaskRow);
    }

    @FXML
    void textFieldKeyPressed(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER) {
            addNewRowAction();
        }
    }

    @FXML
    void deleteCurrentFile() throws IOException {
        viewModel.deleteCurrentFile();
        // Remove the todo panel
        TabPane tabPane = ((TabPane) pane.getParent().getParent());
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.getSelectedItem().setContent(null);
    }

    public void loadFile(String filePath) throws IOException {
        // create Gson instance
        Gson gson = new Gson();

        // create a reader
        Reader reader = Files.newBufferedReader(Paths.get(filePath));

        // convert JSON file to map
        Map<?, ?> map = gson.fromJson(reader, Map.class);

        // Remove all standard kanbans
        boardsContainer.getChildren().clear();

        // Add each map entry as board with tasks
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/views/kanban/TaskRow.fxml"));
            // Add the board
            boardsContainer.getChildren().add(loader.load());
            TaskRowController taskRowController = loader.getController();
            taskRowController.taskModel.setName((String) entry.getKey());
            Map<?, ?> todoMap = gson.fromJson((String) entry.getValue(), Map.class);
            for (Map.Entry<?, ?> todoMapEntry : todoMap.entrySet()) {
                FXMLLoader todoLoader = new FXMLLoader();
                todoLoader.setLocation(getClass().getResource("/fxml/views/todo/todo_item.fxml"));
                taskRowController.getTodoContainer().getChildren().add(todoLoader.load());
                TodoitemController todoController = todoLoader.getController();
                todoController.setTodo((String) todoMapEntry.getKey());
                todoController.setDate(LocalDate.parse((String) todoMapEntry.getValue()));
            }
        }

        // close reader
        reader.close();
    }

    public void saveFile(String filePath) throws IOException {
        // Create Gson instance
        Gson gson = new Gson();
        // create a writer
        Writer writer = Files.newBufferedWriter(Paths.get(filePath));
        // Write the kanban rows
        Map<String, Object> taskRowsMap = new HashMap<>();
        for(Node row:boardsContainer.getChildren()){
            taskRowModel taskRowModel = ((TaskRowController) row.getUserData()).taskModel;
            Map<String, Object> tasksMap = new HashMap<>();
            for(Node task:(((TaskRowController) row.getUserData()).getTodoContainer()).getChildren()) {
                TodoitemController todoController = ((TodoitemController) task.getUserData());
                tasksMap.put(todoController.getTodo(), todoController.getDateAsString());
            }
            taskRowsMap.put(taskRowModel.getName(), gson.toJson(tasksMap));
        }
        gson.toJson(taskRowsMap, writer);
        // close writer
        writer.close();
    }

    public void saveCurrentFile() throws IOException {
    }
}