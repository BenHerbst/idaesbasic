package org.ainm.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.Desktop;

public class MainController {

	List<String> registered_projects = new ArrayList<String>();
	String current_project_path = "";
	
    @FXML
    private Button date_button;

    @FXML
    private Button time_button;

    @FXML
    private TreeView<String> file_explorer;
    
    @FXML
    private TabPane tab_pane;
    
    @FXML
    private Menu RegisteredProjectsListMenu;
  
    @FXML
    void initialize() {
    	//Set date from date button
    	date_button.setText(LocalDate.now().toString());
    	Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//Updates the time button every 2 seconds
				int minutes = LocalTime.now().getMinute();
				int hours = LocalTime.now().getHour();
				Platform.runLater(() -> time_button.setText(Integer.toString(hours) + ((minutes < 10) ? ":0" : ":") + Integer.toString(minutes)));
			}
        }, 0, 2000);
    }

    void add_subdirs(String directory, TreeItem<String> treeItem) throws IOException {
    	//List all sub dirs
    	File[] directories = new File(directory).listFiles(File::isDirectory);
    	for (File dir:directories) {
    		//Add every subdir as tree item to its parent's tree item
    		TreeItem<String> currentDirTreeItem = new TreeItem<>(dir.getName());
    		treeItem.getChildren().add(currentDirTreeItem);
    		//Add all subdirs / files from every subdir to tree
    		add_subdirs(dir.getAbsolutePath(), currentDirTreeItem);
    	}
    	//List all sub files
    	File[] files = new File(directory).listFiles(File::isFile);
    	for (File file:files) {
    		//Add every subfile as tree item to its parent's tree item
    		TreeItem<String> currentFileTreeItem = new TreeItem<>(file.getName());
    		treeItem.getChildren().add(currentFileTreeItem);
    	}
	}
    
    void open_registered_project(String directory) {
    	current_project_path = directory;
        Path rootFile = Paths.get(directory);
        TreeItem<String> rootItem = new TreeItem<>(rootFile.getFileName().toString());
        file_explorer.setRoot(rootItem);
        try {
        	//Add all subdirs and subfiles to the tree root
			add_subdirs(directory, rootItem);
		} catch (IOException e) {
			e.printStackTrace();
		}
        rootItem.setExpanded(true);
    }

    @FXML
    void add_project(ActionEvent event) {
    	//Register a new project and open it
    	DirectoryChooser chooser = new DirectoryChooser();
    	File project = chooser.showDialog(null);
    	registered_projects.add(project.toString());
    	//Add menuitem from added project to the registered projects list menu
    	MenuItem projectMenuItem = new MenuItem();
    	projectMenuItem.setText(Paths.get(project.toString()).getFileName().toString());
    	projectMenuItem.setOnAction(e -> {
    		//Set open action to open the project, when clicked
    		open_registered_project(project.toString());
    	});
    	RegisteredProjectsListMenu.getItems().add(projectMenuItem);
    	open_registered_project(project.toString());
    }
    
    @FXML
    void delete_current_project(ActionEvent event) {
    	close_project();
    	//Remove the current project from the registered list
    	registered_projects.remove(current_project_path);
    	createProjectList();
    }

    void createProjectList() {
    	//Delete all projects from RegisteredProjectsListMenu
    	RegisteredProjectsListMenu.getItems().removeAll(RegisteredProjectsListMenu.getItems());
    	//Add every project from registered_projects to the RegisteredProjectsListMenu
    	for (String project_path:registered_projects) {
    		MenuItem projectMenuItem = new MenuItem();
        	projectMenuItem.setText(Paths.get(project_path).getFileName().toString());
        	projectMenuItem.setOnAction(e -> {
        		//Set open action to open the project, when clicked
        		open_registered_project(project_path);
        	});
    		RegisteredProjectsListMenu.getItems().add(projectMenuItem);
    	}
    }

    @FXML
    void close_current_project(ActionEvent event) {
    	close_project();
    }
    
    void close_project() {
    	//Close the file explorer
    	file_explorer.setRoot(null);
    }
    
    @FXML
    void open_new_window(ActionEvent event) {
    	try {
    		//Open a new window in same process
    		Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
    		Scene scene = new Scene(root, 640, 480);
    		Stage stage = new Stage();
    		stage.setScene(scene);
    		stage.setTitle("Ainm - 0.01 / Todolist");
    		stage.show();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    @FXML
    void show_github_site(ActionEvent event) throws IOException, URISyntaxException {
    	//Shows the github side of the project in the current standard browser
    	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    	    Desktop.getDesktop().browse(new URI("https://github.com/BenHerbst/ainm"));
    	}
    }

    @FXML
    void close_current_window(ActionEvent event) {
    	//Close the current stage
    	date_button.getScene().getWindow().hide();
    }
    
    @FXML
    void close_app(ActionEvent event) {
    	//Fully close the app
    	Platform.exit();
    	System.exit(0);
    }
    
    @FXML
    void open_default_browser(ActionEvent event) throws IOException, URISyntaxException {
    	//Shows the github side of the project in the current standard browser
    	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    	    Desktop.getDesktop().browse(new URI("https://duckduckgo.com/"));
    	}
    }

    @FXML
    void open_default_mail(ActionEvent event) throws IOException, URISyntaxException {
    	//Shows the github side of the project in the current standard browser
    	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    	    Desktop.getDesktop().mail();
    	}
    }
    
    @FXML
    void open_file_explorer(ActionEvent event) {
    	//Open the file explorer in the user directory
    	File file = new File(System.getProperty("user.home"));
    	try {
    		if(Desktop.isDesktopSupported()) {
    			Desktop desktop = Desktop.getDesktop();
    			desktop.open(file);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    @FXML
    void add_tab(ActionEvent event) {
    	//Open a new empty tab
    	Tab newTab = new Tab();
    	newTab.setText("New tab");
    	tab_pane.getTabs().add(newTab);
    	//Select the tab
    	SingleSelectionModel<Tab> selectionModel = tab_pane.getSelectionModel();
    	selectionModel.select(newTab); 
    }
    
    @FXML
    void close_current_tab(ActionEvent event) {
    	//Close the current tab
    	SingleSelectionModel<Tab> selectionModel = tab_pane.getSelectionModel();
    	Tab currentTab = selectionModel.getSelectedItem();
    	tab_pane.getTabs().remove(currentTab);
    }
    
    @FXML
    void close_all_tabs(ActionEvent event) {
    	//Close all tabs of tabpane
    	tab_pane.getTabs().clear();
    }
}