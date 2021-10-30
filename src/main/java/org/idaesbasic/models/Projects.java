package org.idaesbasic.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Projects {

    private final ListProperty<String> registeredProjects = new SimpleListProperty<String>();
    
    private final StringProperty currentProjectPath = new SimpleStringProperty();
    
    public void addProjectToRegisteredProjects(String project) {
        registeredProjects.add(project.toString());
    }

    public void removeCurrentProjectFromRegisteredProjects() {
        registeredProjects.remove(currentProjectPath);
    }

    public void setCurrentProjectPath(String projectPath) {
        currentProjectPath.set(projectPath);
    }

    public String getCurrentProjectPath() {
        return currentProjectPath.getValue();
    }

    public void loadProjectListFromUserFiles() throws JSONException, IOException {
        List<String> newProjectList = new ArrayList<>();
        for (Object project:(JSONArray) new ConfigFilesLoader().load("registeredProjects")) {
            newProjectList.add((String) project);
        }
        registeredProjects.set(FXCollections.observableArrayList(newProjectList));
    }

    public void saveProjectListToUserFiles() throws IOException {
        // Saves the registered projects in an json file
        // Create json
        JSONObject json = new JSONObject();
        json.put("registeredProjects", registeredProjects.getValue());
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

    public List<String> getProjectList() {
        return registeredProjects.getValue();
    }
}