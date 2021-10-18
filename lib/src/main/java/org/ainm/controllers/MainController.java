package org.ainm.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {
	
    @FXML
    private Button date_button;

    @FXML
    private Button time_button;

    @FXML
    private TreeView<String> file_explorer;
  
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
        Path rootFile = Paths.get(directory);
        TreeItem<String> rootItem = new TreeItem<>(rootFile.getFileName().toString());
        file_explorer.setRoot(rootItem);
        try {
        	//Add all subdirs and subfiles to the tree root
			add_subdirs(directory, rootItem);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @FXML
    void add_project(ActionEvent event) {
    	DirectoryChooser chooser = new DirectoryChooser();
    	File project = chooser.showDialog(null);
    	open_registered_project(project.toString());
    }
}