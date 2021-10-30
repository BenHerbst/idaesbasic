package org.idaesbasic.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigFilesLoader {

    public Object load(String key) throws JSONException, IOException {
        String userDirectoryPath = System.getProperty("user.home") + "/.ideasbasic";
        Path userDirectory = Paths.get(userDirectoryPath);
        if(Files.exists(userDirectory)) {
            JSONObject parser = new JSONObject(Files.readString(Paths.get(userDirectoryPath + "/config.json")));
            //Get the given key from the configs
            return parser.get(key);
        } else {
            return new Object();
        }
    }
}