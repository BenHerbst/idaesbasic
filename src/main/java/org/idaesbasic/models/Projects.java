package org.idaesbasic.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class jProjects {

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

    public void loadProjectListFromUserFiles() throws IOException {
        Map map = new ConfigFilesLoader().loadConfigs();
        if(map != null) {
            List<String> newProjectList = new ArrayList<>();
            for (Object project:(List<String>) map.get("registeredProjects")) {
                newProjectList.add((String) project);
            }
            registeredProjects.set(FXCollections.observableArrayList(newProjectList));
    }
    }

    public void saveProjectListToUserFiles() throws IOException {
        // Saves the registered projects in an json file
        // Create json
        Map<String, Object> map = new HashMap<>();
        map.put("registeredProjects", registeredProjects.getValue());
        // create object mapper instance
        ObjectMapper mapper = new ObjectMapper();

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
        mapper.writeValue(file.toFile(), map);
    }

    public List<String> getProjectList() {
        return registeredProjects.getValue();
    }
}