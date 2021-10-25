package org.idaesbasic.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Load {
	public List<String> loadProjectList() throws IOException {
		// Read the config.json file
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if(Files.exists(userDirectory)) {
            JSONObject parser = new JSONObject(Files.readString(Paths.get(userDirectoryPath + "/config.json")));
            //Get the registered projects from parser
            List<String> registeredProjects = new ArrayList<String>();
            for (Object project:parser.getJSONArray("registeredProjects")) {
            	registeredProjects.add((String) project);
            }
            return registeredProjects;
        }
        return null;
	}
}