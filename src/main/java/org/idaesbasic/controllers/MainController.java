package org.idaesbasic.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
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

import org.idaesbasic.controllers.todolist.TodolistController;
import org.idaesbasic.data.Load;

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
import javafx.stage.Window;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;

import org.json.*;

public class MainController {

    public class NewFileDialogResult {
        private Optional<ButtonType> result;
        private String directory;
        private String filename;

        public NewFileDialogResult(Optional<ButtonType> result, String directory, String filename) {
            this.result = result;
            this.directory = directory;
            this.filename = filename;
        }
    }

    List<String> registered_projects = new ArrayList<>();
    String currentProjectPath = "";

    @FXML
    private Button dateButton;

    @FXML
    private Button timeButton;

    @FXML
    private TreeView<File> fileExplorer;

    @FXML
    private TabPane tabPane;

    @FXML
    private Menu RegisteredProjectsListMenu;

    @FXML
    void initialize() {
        try {
            loadProjectList();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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
                        .setText(Integer.toString(hours) + ((minutes < 10) ? ":0" : ":") + Integer.toString(minutes)));
            }
        }, 0, 2000);
        fileExplorer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
            addSubdirs(dir.getAbsolutePath(), currentDirTreeItem);
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
        currentProjectPath = directory;
        Path rootFile = Paths.get(directory);
        TreeItem<File> rootItem = new TreeItem<>(new File(directory));
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
    void addProject(ActionEvent event) {
        // Register a new project and open it
        DirectoryChooser chooser = new DirectoryChooser();
        addNewProject(chooser.showDialog(null));
    }

    void addNewProject(File project) {
        registered_projects.add(project.toString());
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
            saveProjectList();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @FXML
    void deleteCurrentProject(ActionEvent event) throws IOException {
        // Remove the current project from the registered list
        registered_projects.remove(currentProjectPath);
        createProjectList();
        closeProject();
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
                openRegisteredProject(project_path);
            });
            RegisteredProjectsListMenu.getItems().add(projectMenuItem);
        }
    }

    @FXML
    void closeCurrentProject(ActionEvent event) throws IOException {
        closeProject();
    }

    void closeProject() throws IOException {
        // Close the file explorer
        fileExplorer.setRoot(null);
        currentProjectPath = "";
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
            stage.setTitle("Idaesbasic - Child window - 0.8.0 - Alpha");
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
        DialogPane usedLibarys = FXMLLoader.load(getClass().getResource("/fxml/dialogs/usedLibarys.fxml"));
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
    void addTab(ActionEvent event) throws IOException {
        // Open a new empty tab
        Tab newTab = new Tab();
        newTab.setText("New tab");
        tabPane.getTabs().add(newTab);
        // Select the tab
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(newTab);
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
    }

    @FXML
    void closeFile(ActionEvent event) {
        // Closes the current file and make the tab content empty
        get_current_tab().setContent(null);
    }

    @FXML
    void newTodolist(ActionEvent event) throws IOException {
        if (currentProjectPath != "") {
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
                }
                // Load the todolist view in current tab
                FXMLLoader loader = new FXMLLoader();
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
    void newCalendar(ActionEvent event) throws IOException {
        if (currentProjectPath != "") {
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
                }
                CalendarView calendarView = new CalendarView();
                // Create work calendars
                Calendar meetingsCalendar = new Calendar("Meetings");
                Calendar pausesCalendar = new Calendar("Pauses");
                Calendar vacationsCalendar = new Calendar("Vacation");
                meetingsCalendar.setStyle(Style.STYLE2);
                pausesCalendar.setStyle(Style.STYLE1);
                vacationsCalendar.setStyle(Style.STYLE3);
                CalendarSource workCalendarSource = new CalendarSource("Work");
                workCalendarSource.getCalendars().addAll(meetingsCalendar, pausesCalendar, vacationsCalendar);
                // Create private calendars
                Calendar birthdaysCalendar = new Calendar("Birthdays");
                Calendar appointmentsCalendar = new Calendar("Appointments");
                Calendar eventsCalendar = new Calendar("Events");
                birthdaysCalendar.setStyle(Style.STYLE5);
                appointmentsCalendar.setStyle(Style.STYLE6);
                eventsCalendar.setStyle(Style.STYLE4);
                CalendarSource privateCalendarSource = new CalendarSource("Private");
                privateCalendarSource.getCalendars().addAll(birthdaysCalendar, appointmentsCalendar, eventsCalendar);
                // Create sport calendars
                Calendar cyclingCalendar = new Calendar("Cylcling");
                Calendar joggingCalendar = new Calendar("Jogging");
                Calendar swimingCalendar = new Calendar("Swiming");
                cyclingCalendar.setStyle(Style.STYLE4);
                joggingCalendar.setStyle(Style.STYLE7);
                swimingCalendar.setStyle(Style.STYLE2);
                CalendarSource sportCalendarSource = new CalendarSource("Sport");
                sportCalendarSource.getCalendars().addAll(cyclingCalendar, joggingCalendar, swimingCalendar);
                // Add calendars to view
                calendarView.getCalendarSources().addAll(workCalendarSource, privateCalendarSource,
                        sportCalendarSource);
                // Update current calendar time
                calendarView.setRequestedTime(LocalTime.now());
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
//                      Update the calendar view current day and time every 10 secounds
                        Platform.runLater(() -> {
                            calendarView.setToday(LocalDate.now());
                            calendarView.setTime(LocalTime.now());
                        });
                    }
                }, 0, 2000);
                // Show new calendar in current tab
                addViewToCurrentTab(calendarView);
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
        createFileDialogController.setDirectoryField(currentProjectPath + "/");
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
            // Set controller as user data for later access
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
        // Saves the registered projects in an json file
        // Create json
        JSONObject json = new JSONObject();
        json.put("registeredProjects", registered_projects);
        // Write json to file
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if (!Files.exists(userDirectory)) {
            Files.createDirectory(userDirectory);
        }
        Path file = Paths.get(userDirectoryPath + "/config.json");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        Files.writeString(file, json.toString());
    }

    void loadProjectList() throws IOException {
        registered_projects = new Load().loadProjectList();
        // Create the listmenu
        if (registered_projects != null)
            createProjectList();
    }

    Tab get_current_tab() {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
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
