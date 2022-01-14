package org.idaesbasic.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.idaesbasic.controllers.calendar.CalendarController;
import org.idaesbasic.controllers.todolist.TodolistController;
import org.idaesbasic.models.Projects;
import org.idaesbasic.controllers.kanban.KanbanController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.fortuna.ical4j.data.ParserException;

public class MainController {

    public class NewFileDialogResult {
        private final Optional<ButtonType> result;
        private final String directory;
        private final String filename;

        public NewFileDialogResult(Optional<ButtonType> result, String directory, String filename) {
            this.result = result;
            this.directory = directory;
            this.filename = filename;
        }
    }

    Projects projectModel = new Projects();

    @FXML
    private Button dateButton;

    @FXML
    private Button timeButton;

    @FXML
    private Tab plusTab;

    @FXML
    private TreeView<String> fileExplorer;

    @FXML
    private TabPane tabPane;

    @FXML
    private Menu RegisteredProjectsListMenu;

    @FXML
    private MenuItem menuItemNewTab;

    @FXML
    private MenuItem menuItemNewWindow;

    @FXML
    private MenuItem menuItemSaveCurrentFile;

    @FXML
    private MenuItem menuItemSaveAs;

    @FXML
    private MenuItem menuItemCloseTab;

    @FXML
    void initialize() {
        try {
            projectModel.loadProjectListFromUserFiles();
            createProjectList();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // Add new tab when "add tab" tab is selected
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if(newTab.equals (plusTab)) {
                    addNewTab();
                }
            }
        });
        // Set date from date button
        dateButton.setText(LocalDate.now().toString());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Updates file explorer
                // if (currentProjectPath != "") {
                // Platform.runLater(() -> openRegisteredProject(currentProjectPath));
                // }
                // Updates the time button every 2 seconds
                int minutes = LocalTime.now().getMinute();
                int hours = LocalTime.now().getHour();
                Platform.runLater(() -> timeButton
                        .setText(hours + ((minutes < 10) ? ":0" : ":") + minutes));
            }
        }, 0, 2000);
        fileExplorer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                openFile(getPathOf(newValue));
            } catch (IOException | ParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        // Add shortcuts
        menuItemNewTab.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        menuItemNewWindow.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuItemSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        menuItemSaveCurrentFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuItemCloseTab.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
    }

    void reloadFileExplorer(TreeItem<String> item) throws IOException {
        item.getChildren().clear();
        addSubdirs(getPathOf(item), item);
    }

    void addSubdirs(String directory, TreeItem<String> treeItem) throws IOException {
        // List all sub dirs
        File[] directories = new File(directory).listFiles(File::isDirectory);
        for (File dir : directories) {
            // Add every subdir as tree item to its parent's tree item
            TreeItem<String> currentDirTreeItem = new TreeItem<>(dir.getName());
            treeItem.getChildren().add(currentDirTreeItem);
            // Add all subdirs / files from every subdir to tree
            addSubdirs(dir.getAbsolutePath(), currentDirTreeItem);
        }
        // List all sub files
        File[] files = new File(directory).listFiles(File::isFile);
        for (File file : files) {
            // Add every subfile as tree item to its parent's tree item
            TreeItem<String> currentFileTreeItem = new TreeItem<>(file.getName());
            treeItem.getChildren().add(currentFileTreeItem);
        }
    }

    String getPathOf(TreeItem<String> treeItem) {
        String currentPath = treeItem.getValue();
        if (treeItem.getParent() != null) {
            currentPath = getPathOf(treeItem.getParent()) + "/" + currentPath;
        }
        return currentPath;
    }

    void openRegisteredProject(String directory) {
        projectModel.setCurrentProjectPath(directory);
        Path rootFile = Paths.get(directory);
        TreeItem<String> rootItem = new TreeItem<>(directory);
        fileExplorer.setRoot(rootItem);
        try {
            // Add all subdirs and subfiles to the tree root
            addSubdirs(directory, rootItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootItem.setExpanded(true);
    }

    @FXML
    void addProject(ActionEvent event) throws IOException {
        // Register a new project and open it
        DirectoryChooser chooser = new DirectoryChooser();
        addNewProject(chooser.showDialog(null));
    }

    void addNewProject(File project) throws IOException {
        projectModel.addProjectToRegisteredProjects(project.toString());
        // Add menuitem from added project to the registered projects list menu
        MenuItem projectMenuItem = new MenuItem();
        projectMenuItem.setText(Paths.get(project.toString()).getFileName().toString());
        projectMenuItem.setOnAction(e -> {
            // Set open action to open the project, when clicked
            openRegisteredProject(project.toString());
        });
        RegisteredProjectsListMenu.getItems().add(projectMenuItem);
        openRegisteredProject(project.toString());
        try {
            projectModel.saveProjectListToUserFiles();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @FXML
    void deleteCurrentProject(ActionEvent event) throws IOException {
        // Remove the current project from the registered list
        projectModel.removeCurrentProjectFromRegisteredProjects();
        projectModel.saveProjectListToUserFiles();
        closeProject();
    }

    void createProjectList() throws IOException {
        // Delete all projects from RegisteredProjectsListMenu
        RegisteredProjectsListMenu.getItems().removeAll(RegisteredProjectsListMenu.getItems());
        if(projectModel.getProjectList() != null) {
            // Add every project from registered projects to the RegisteredProjectsListMenu
            for (String project_path : projectModel.getProjectList()) {
                MenuItem projectMenuItem = new MenuItem();
                projectMenuItem.setText(Paths.get(project_path).getFileName().toString());
                projectMenuItem.setOnAction(e -> {
                    // Set open action to open the project, when clicked
                    openRegisteredProject(project_path);
                });
                RegisteredProjectsListMenu.getItems().add(projectMenuItem);
            }
        }
    }

    @FXML
    void closeProjectAction(ActionEvent event) throws IOException {
        closeProject();
    }

    void closeProject() throws IOException {
        // Close the file explorer
        fileExplorer.setRoot(null);
        projectModel.setCurrentProjectPath("");
        // Load welcome screen
        Parent welcomeScreen = FXMLLoader.load(getClass().getResource("/fxml/WelcomeScreen.fxml"));
        // Configre stage
        Scene scene = new Scene(welcomeScreen);
        Stage stage = (Stage) getCurrentStage();
        stage.hide();
        stage.setHeight(530);
        stage.setWidth(650);
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.setTitle("Idaesbasic / Welcome - 0.9.0 - Beta");
        stage.show();
        stage.setScene(scene);
    }

    @FXML
    void openNewWindow(ActionEvent event) {
        try {
            // Open a new window in same process
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 640, 480);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Idaesbasic -  0.9.0 - Beta");
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
    void showUsedLibarys(ActionEvent event) throws IOException {
        DialogPane usedLibarys = FXMLLoader.load(getClass().getResource("/fxml/dialogs/UsedLibarys.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Used libarys");
        dialog.setDialogPane(usedLibarys);
        dialog.show();
    }

    @FXML
    void closeCurrentWindow(ActionEvent event) {
        // Close the current stage
        getCurrentStage().hide();
    }

    Window getCurrentStage() {
        return dateButton.getScene().getWindow();
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
    void openDefaultMail(ActionEvent event) throws IOException, URISyntaxException {
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
    void addTabAction(ActionEvent event) throws IOException {
        addNewTab();
    }

    void addNewTab() {
        // Open a new empty tab
        Tab newTab = new Tab();
        newTab.setText("New tab");
        tabPane.getTabs().add(newTab);
        // Select the tab
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(newTab);
        // Remove and re-add the "add tab" tab, to have it always as last tab
        tabPane.getTabs().remove(plusTab);
        tabPane.getTabs().add(plusTab);
    }

    @FXML
    void closeCurrentTab(ActionEvent event) {
        // Close the current tab
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        Tab currentTab = selectionModel.getSelectedItem();
        tabPane.getTabs().remove(currentTab);
    }

    @FXML
    void closeAllTabs(ActionEvent event) {
        // Close all tabs of tabpane
        tabPane.getTabs().clear();
        tabPane.getTabs().add(plusTab);
    }

    @FXML
    void closeFile(ActionEvent event) {
        // Closes the current file and make the tab content empty
        getCurrentTab().setContent(null);
    }

    @FXML
    void newTodolist(ActionEvent event) throws IOException {
        if (projectModel.getCurrentProjectPath() != "") {
            NewFileDialogResult dialogResult = showCreateNewFileDialog(".todo");
            Optional<ButtonType> result = dialogResult.result;
            if (result.get() == ButtonType.FINISH) {
                // Create the file
                String directoryPath = dialogResult.directory;
                String fileName = dialogResult.filename;
                String fullPath = ((directoryPath.endsWith("/")) ? directoryPath : directoryPath + "/") + fileName
                        + ".todo";
                File file = new File(fullPath);
                if (!file.exists()) {
                    file.createNewFile();
                    reloadFileExplorer(currentTreeItem());
                }
                // Load the todolist view in current tab
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/views/todo/TodoView.fxml"));
                Node todolistView = loader.load();
                TodolistController controller = loader.getController();
                todolistView.setUserData(controller);
                controller.viewModel.setOpenedFile(Paths.get(fullPath));
                addViewToCurrentTab(todolistView);
            }
        }
    }

    @FXML
    void newCalendar(ActionEvent event) throws IOException {
        if (projectModel.getCurrentProjectPath() != "") {
            NewFileDialogResult dialogResult = showCreateNewFileDialog(".ics");
            Optional<ButtonType> result = dialogResult.result;
            if (result.get() == ButtonType.FINISH) {
                // Create the file
                String directoryPath = dialogResult.directory;
                String fileName = dialogResult.filename;
                String fullPath = ((directoryPath.endsWith("/")) ? directoryPath : directoryPath + "/") + fileName
                        + ".ics";
                File file = new File(fullPath);
                if (!file.exists()) {
                    file.createNewFile();
                    reloadFileExplorer(currentTreeItem());
                }
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/views/calendar/CalendarView.fxml"));
                Node calendarView = loader.load();
                CalendarController controller = loader.getController();
                controller.viewModel.setOpenedFile(Paths.get(fullPath));
                calendarView.setUserData(controller);
                // Show new calendar in current tab
                addViewToCurrentTab(calendarView);
            }
        }
    }

    @FXML
    void newKanban(ActionEvent event) throws IOException {
        if (projectModel.getCurrentProjectPath() != "") {
            NewFileDialogResult dialogResult = showCreateNewFileDialog(".knbn");
            Optional<ButtonType> result = dialogResult.result;
            if (result.get() == ButtonType.FINISH) {
                // Create the file
                String directoryPath = dialogResult.directory;
                String fileName = dialogResult.filename;
                String fullPath = ((directoryPath.endsWith("/")) ? directoryPath : directoryPath + "/") + fileName
                        + ".knbn";
                File file = new File(fullPath);
                if (!file.exists()) {
                    file.createNewFile();
                    reloadFileExplorer(currentTreeItem());
                }
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/views/kanban/KanbanView.fxml"));
                Node view = loader.load();
                KanbanController controller = loader.getController();
                controller.viewModel.setOpenedFile(Paths.get(fullPath));
                view.setUserData(controller);
                // Show new calendar in current tab
                addViewToCurrentTab(view);
            }
        }
    }

    NewFileDialogResult showCreateNewFileDialog(String extention) throws IOException {
        // Shows the create a new file dialog
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/dialogs/CreateNewFile.fxml"));
        DialogPane createNewFileDialog = loader.load();
        // Get the control of the dialog
        CreateNewFileDialogController createFileDialogController = loader.getController();
        createFileDialogController.changeExtention(extention);
        createFileDialogController.setDirectoryField(getPathOf(currentTreeItem()) + "/");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(createNewFileDialog);
        dialog.setTitle("Create a new file");
        Optional<ButtonType> result = dialog.showAndWait();
        // Return all properties
        return new NewFileDialogResult(result, createFileDialogController.getDirectory(),
                createFileDialogController.getFilename());
    }

    @FXML
    void saveFileAs() throws IOException {
        // Open file explorer
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(null);
        // Save current view to file
        Object controller = getController(getCurrentTab().getContent());
        if (controller.getClass().equals(new TodolistController().getClass())) {
            ((TodolistController) controller).saveFileAs(Paths.get(file.getAbsolutePath()));
        } else if (controller.getClass().equals(new CalendarController().getClass())) {
            ((CalendarController) controller).saveFile(file.getAbsolutePath());
        } else if (controller.getClass().equals(new KanbanController().getClass())) {
            ((KanbanController) controller).saveFile(file.getAbsolutePath());
        }
    }

    @FXML
    void saveCurrentFile() throws IOException {
        // Save current view to file
        Object controller = getController(getCurrentTab().getContent());
        if (controller.getClass().equals(new TodolistController().getClass())) {
            ((TodolistController) controller).saveCurrentFile();
        } else if (controller.getClass().equals(new CalendarController().getClass())) {
            ((CalendarController) controller).saveCurrentFile();
        } else if (controller.getClass().equals(new KanbanController().getClass())) {
            ((KanbanController) controller).saveCurrentFile();
        }
    }

    void openFile(String filename) throws IOException, ParserException {
        if (filename.endsWith(".todo")) {
            // Load todolist view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/views/todo/TodoView.fxml"));
            Node view = loader.load();
            // Load todolist
            TodolistController todolistController = loader.getController();
            // Set controller as user data for later access
            view.setUserData(todolistController);
            todolistController.loadFile(Paths.get(filename));
            // Open todolist view
            addViewToCurrentTab(view);
        } else if (filename.endsWith(".ics")) {
            // Load calendar view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/views/calendar/CalendarView.fxml"));
            Node view = loader.load();
            // Load calendar
            CalendarController calendarController = loader.getController();
            // Set controller as user data for later access
            view.setUserData(calendarController);
            calendarController.loadFile(filename);
            // Open todolist view
            addViewToCurrentTab(view);
        } else if (filename.endsWith(".knbn")) {
            // Load calendar view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/views/kanban/KanbanView.fxml"));
            Node view = loader.load();
            // Load calendar
            KanbanController kanbanController = loader.getController();
            // Set controller as user data for later access
            view.setUserData(kanbanController);
            kanbanController.loadFile(filename);
            // Open todolist view
            addViewToCurrentTab(view);
        }
    }

    @FXML
    void showAboutDialogAction(ActionEvent event) throws IOException {
        Dialog<ButtonType> aboutDialog = new Dialog<ButtonType>();
        aboutDialog.setDialogPane(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/dialogs/About.fxml"))));
        aboutDialog.setTitle("About Idaesbasic");
        aboutDialog.show();
    }

    void addViewToCurrentTab(Node view) {
        getCurrentTab().setContent(view);
    }

    Tab getCurrentTab() {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        return (selectionModel.getSelectedItem());
    }

    TreeItem<String> currentTreeItem() {
        MultipleSelectionModel<TreeItem<String>> selectionModel = fileExplorer.getSelectionModel();
        if(selectionModel.getSelectedItem() != null) {
            // Non root item is selected
            return (selectionModel.getSelectedItem());
        } else {
            // Root item is selected
            return fileExplorer.getRoot();
        }
    }

    @FXML
    void createNewDirectoryAction(ActionEvent event) throws IOException {
        // Load the create new directory dialog, so the user can enter a dir name
        Dialog<ButtonType> createNewDirectoryDialog = new Dialog<ButtonType>();
        FXMLLoader createNewDirectoryLoader = new FXMLLoader();
        createNewDirectoryLoader.setLocation(getClass().getResource("/fxml/dialogs/CreateNewDirectory.fxml"));
        createNewDirectoryDialog.setDialogPane(createNewDirectoryLoader.load());
        createNewDirectoryDialog.setTitle("Create new directory");
        Optional<ButtonType> result = createNewDirectoryDialog.showAndWait();
        // After clicking on finish, create the new folder
        if (result.get() == ButtonType.FINISH) {
            projectModel.addNewFolder(getPathOf(currentTreeItem()) + "/" + ((CreateNewDirectoryDialogController) createNewDirectoryLoader.getController()).getDirectoryName());}
    }

    @FXML
    void deleteFileAction(ActionEvent event) throws IOException {
        // Delete currently selected file
        File toDeleteFile = new File(getPathOf(currentTreeItem()));
        if (toDeleteFile.isDirectory()) {
            FileUtils.deleteDirectory(toDeleteFile);
        } else {
            FileUtils.delete(toDeleteFile);
        }
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
