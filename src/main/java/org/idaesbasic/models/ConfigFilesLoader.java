package org.idaesbasic.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigFilesLoader {

    public Map loadConfigs() throws IOException {
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if(Files.exists(userDirectory)) {
            ObjectMapper mapper = new ObjectMapper();

            // Convert JSON file to map
            Map<?, ?> map = mapper.readValue(Paths.get(userDirectoryPath + "/config.json").toFile(), Map.class);

            // Return the map
            return map;
        } else {
            return null;
        }
    }
}