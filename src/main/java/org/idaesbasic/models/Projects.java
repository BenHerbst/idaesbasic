package org.idaesbasic.models;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Projects {

    private final ListProperty<String> registeredProjects = new SimpleListProperty<String>(FXCollections.observableArrayList());

    private final StringProperty currentProjectPath = new SimpleStringProperty();

    public void addProjectToRegisteredProjects(String project) throws IOException {
        registeredProjects.add(project);
        saveProjectListToUserFiles();
    }

    public void removeCurrentProjectFromRegisteredProjects() {
        registeredProjects.remove(getCurrentProjectPath());
    }

    public void setCurrentProjectPath(String projectPath) {
        currentProjectPath.set(projectPath);
    }

    public String getCurrentProjectPath() {
        return currentProjectPath.getValue();
    }

    public void loadProjectListFromUserFiles() throws IOException {
        Map map = new ConfigFilesLoader().loadConfigs();
        System.out.println(map);
        if (map != null) {
            List<String> newProjectList = new ArrayList<>();
            for (Object project : (List<String>) map.get("registeredProjects")) {
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
        Gson gson = new Gson();

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
        Writer writer = Files.newBufferedWriter(file);
        gson.toJson(map, writer);
        writer.close();
    }

    public List<String> getProjectList() {
        return registeredProjects.getValue();
    }

    public void addNewFolder(String newFolderPath) throws IOException {
        Files.createDirectory(Paths.get(newFolderPath));
    }
}