package org.ainm.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.ainm.controllers.todolist.TodolistController;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

    List<String> registered_projects = new ArrayList<>();
    String currentProjectPath = "";

    @FXML
    private Button dateButton;

    @FXML
    private Button timeButton;

    @FXML
    private TreeView<File> file_explorer;

    @FXML
    private TabPane tabPane;

    @FXML
    private Menu RegisteredProjectsListMenu;

    @FXML
    void initialize() {
        try {
            loadProjectList();
        } catch (IOException | JsonException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // Set date from date button
        date_button.setText(LocalDate.now().toString());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Updates file explorer
                // if (current_project_path != "") {
                // Platform.runLater(() -> open_registered_project(current_project_path));
                // }
                // Updates the time button every 2 seconds
                int minutes = LocalTime.now().getMinute();
                int hours = LocalTime.now().getHour();
                Platform.runLater(() -> time_button
                        .setText(Integer.toString(hours) + ((minutes < 10) ? ":0" : ":") + Integer.toString(minutes)));
            }
        }, 0, 2000);
        file_explorer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                openFile(newValue.getValue().getAbsolutePath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    void addSubdirs(String directory, TreeItem<File> treeItem) throws IOException {
        // List all sub dirs
        File[] directories = new File(directory).listFiles(File::isDirectory);
        for (File dir : directories) {
            // Add every subdir as tree item to its parent's tree item
            TreeItem<File> currentDirTreeItem = new TreeItem<>(dir.getAbsoluteFile());
            treeItem.getChildren().add(currentDirTreeItem);
            // Add all subdirs / files from every subdir to tree
            add_subdirs(dir.getAbsolutePath(), currentDirTreeItem);
        }
        // List all sub files
        File[] files = new File(directory).listFiles(File::isFile);
        for (File file : files) {
            // Add every subfile as tree item to its parent's tree item
            TreeItem<File> currentFileTreeItem = new TreeItem<>(file.getAbsoluteFile());
            treeItem.getChildren().add(currentFileTreeItem);
        }
    }

    void openRegisteredProject(String directory) {
        current_project_path = directory;
        Path rootFile = Paths.get(directory);
        TreeItem<File> rootItem = new TreeItem<>(new File(directory));
        file_explorer.setRoot(rootItem);
        try {
            // Add all subdirs and subfiles to the tree root
            add_subdirs(directory, rootItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootItem.setExpanded(true);
    }

    @FXML
    void addProject(ActionEvent event) {
        // Register a new project and open it
        DirectoryChooser chooser = new DirectoryChooser();
        File project = chooser.showDialog(null);
        registered_projects.add(project.toString());
        // Add menuitem from added project to the registered projects list menu
        MenuItem projectMenuItem = new MenuItem();
        projectMenuItem.setText(Paths.get(project.toString()).getFileName().toString());
        projectMenuItem.setOnAction(e -> {
            // Set open action to open the project, when clicked
            openRegisteredProject(project.toString());
        });
        RegisteredProjectsListMenu.getItems().add(projectMenuItem);
        open_registered_project(project.toString());
        try {
            saveProjectList();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @FXML
    void deleteCurrentProject(ActionEvent event) {
        // Remove the current project from the registered list
        registered_projects.remove(current_project_path);
        createProjectList();
        close_project();
        try {
            saveProjectList();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    void createProjectList() {
        // Delete all projects from RegisteredProjectsListMenu
        RegisteredProjectsListMenu.getItems().removeAll(RegisteredProjectsListMenu.getItems());
        // Add every project from registered_projects to the RegisteredProjectsListMenu
        for (String project_path : registered_projects) {
            MenuItem projectMenuItem = new MenuItem();
            projectMenuItem.setText(Paths.get(project_path).getFileName().toString());
            projectMenuItem.setOnAction(e -> {
                // Set open action to open the project, when clicked
                open_registered_project(project_path);
            });
            RegisteredProjectsListMenu.getItems().add(projectMenuItem);
        }
    }

    @FXML
    void closeCurrentProject(ActionEvent event) {
        close_project();
    }

    void closeProject() {
        // Close the file explorer
        fileExplorer.setRoot(null);
        currentProjectPath = "";
    }

    @FXML
    void openNewWindow(ActionEvent event) {
        try {
            // Open a new window in same process
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Idaes - 0.8 - Alpha");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showGithubSite(ActionEvent event) throws IOException, URISyntaxException {
        // Shows the github side of the project in the current standard browser
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://github.com/BenHerbst/ainm"));
        }
    }

    @FXML
    void closeCurrentWindow(ActionEvent event) {
        // Close the current stage
        dateButton.getScene().getWindow().hide();
    }

    @FXML
    void closeApp(ActionEvent event) {
        // Fully close the app
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void openDefaultBrowser(ActionEvent event) throws IOException, URISyntaxException {
        // Shows the github side of the project in the current standard browser
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://duckduckgo.com/"));
        }
    }

    @FXML
    void openDefautMail(ActionEvent event) throws IOException, URISyntaxException {
        // Shows the github side of the project in the current standard browser
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().mail();
        }
    }

    @FXML
    void openFileExplorer(ActionEvent event) {
        // Open the file explorer in the user directory
        File file = new File(System.getProperty("user.home"));
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addTab(ActionEvent event) throws IOException {
        // Open a new empty tab
        Tab newTab = new Tab();
        newTab.setText("New tab");
        tab_pane.getTabs().add(newTab);
        // Select the tab
        SingleSelectionModel<Tab> selectionModel = tab_pane.getSelectionModel();
        selectionModel.select(newTab);
    }

    @FXML
    void closeCurrentTab(ActionEvent event) {
        // Close the current tab
        SingleSelectionModel<Tab> selectionModel = tab_pane.getSelectionModel();
        Tab currentTab = selectionModel.getSelectedItem();
        tab_pane.getTabs().remove(currentTab);
    }

    @FXML
    void closeAllTabs(ActionEvent event) {
        // Close all tabs of tabpane
        tab_pane.getTabs().clear();
    }

    @FXML
    void closeFile(ActionEvent event) {
        // Closes the current file and make the tab content empty
        get_current_tab().setContent(null);
    }

    @FXML
    void newTosoList(ActionEvent event) throws IOException {
        if (current_project_path != "") {
            // Shows the create a new file dialog
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/dialogs/CreateNewFile.fxml"));
            DialogPane createNewFileDialog = loader.load();
            // Get the control of the dialog
            CreateNewFileDialogController createFileDialogController = loader.getController();
            createFileDialogController.changeExtention(".todo");
            createFileDialogController.setDirectoryField(current_project_path + "/");
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(createNewFileDialog);
            dialog.setTitle("Create a new file");
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.get() == ButtonType.FINISH) {
                // Create the file
                String directoryPath = createFileDialogController.getDirectory();
                String fileName = createFileDialogController.getFilename();
                String fullPath = ((directoryPath.endsWith("/")) ? directoryPath : directoryPath + "/") + fileName
                        + ".todo";
                File file = new File(fullPath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                // Load the todolist view in current tab
                loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/views/todo/todo_view.fxml"));
                Node todolistView = loader.load();
                TodolistController controller = loader.getController();
                todolistView.setUserData(controller);
                controller.openedFile = Paths.get(fullPath);
                addViewToCurrentTab(todolistView);
            }
        }
    }

    @FXML
    void saveFileAs() throws IOException {
        // Open file explorer
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(null);
        // Save current view to file
        Object controller = getController(get_current_tab().getContent());
        if (controller.getClass().equals(new TodolistController().getClass())) {
            ((TodolistController) controller).saveFileAs(Paths.get(file.getAbsolutePath()));
        }
    }

    @FXML
    void saveCurrentFile() throws IOException {
        // Save current view to file
        Object controller = getController(get_current_tab().getContent());
        if (controller.getClass().equals(new TodolistController().getClass())) {
            ((TodolistController) controller).saveCurrentFile();
        }
    }

    void openFile(String filename) throws IOException {
        if (filename.endsWith(".todo")) {
            // Load todolist view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/views/todo/todo_view.fxml"));
            Node view = loader.load();
            // Load todolist
            TodolistController todolistController = loader.getController();
            //Set controller as user data for later access
            view.setUserData(todolistController);
            todolistController.loadFile(Paths.get(filename));
            // Open todolist view
            addViewToCurrentTab(view);
        }
    }

    void addViewToCurrentTab(Node view) {
        get_current_tab().setContent(view);
    }

    void saveProjectList() throws IOException {
        //Saves the registered projects in an json file
        //Create json
        JsonObject json = new JsonObject();
        json.put("registeredProjects", registered_projects);
        //Write json to file
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if (!Files.exists(userDirectory)) {
            Files.createDirectory(userDirectory);
        }
        Path file = Paths.get(userDirectoryPath + "/config.json");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        Files.writeString(file, json.toJson());
    }
    
    void loadProjectList() throws IOException, JsonException {
        //Read the config.json file
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if(Files.exists(userDirectory)) {
            Reader reader = Files.newBufferedReader(Paths.get(userDirectoryPath + "/config.json"));
            JsonObject parser = (JsonObject) Jsoner.deserialize(reader);
            //Get the registered projects from parser
            registered_projects = (List<String>) parser.get("registeredProjects");
            //Create the listmenu
            createProjectList();
        }
        
    }

    Tab get_current_tab() {
        SingleSelectionModel<Tab> selectionModel = tab_pane.getSelectionModel();
        return (selectionModel.getSelectedItem());
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
