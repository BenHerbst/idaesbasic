package org.idaesbasic.models;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigFilesLoader {

    public Map loadConfigs() throws IOException {
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if(Files.exists(userDirectory)) {
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(userDirectoryPath + "/config.json"));

            // convert JSON string to Book object
            Map<?, ?> map = gson.fromJson(reader, Map.class);

            reader.close();
            return map;
        } else {
            return null;
        }
    }
}