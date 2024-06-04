package com.github.nutt1101.dyucoursetool.utils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

@Component
@Getter
public class ConfigurationJsonLoader {
    private JSONObject jsonObject;

    @PostConstruct
    void load() throws IOException {
        String currentPath = Directory.getCurrentPage();
        String fileName = "/info.json";
        File file = new File(currentPath + fileName);

        if (!file.exists()) {
            this.createExample(file);
            throw new RuntimeException("the file info.json not exists, now file was created");
        }

        String jsonString = Files.readString(file.toPath());
        this.jsonObject = new JSONObject(jsonString);
    }

    private void createExample(File file) throws IOException {
        if (!file.exists()) {
            boolean c = file.createNewFile();
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(
                    """
                        {
                          "info" : [
                            {
                              "id" : "F0000000",
                              "pwd" : "abc",
                              "courses" : [
                                "1111", "1112"
                              ]
                            },
                            {
                              "id" : "F0000001",
                              "pwd" : "abc",
                              "courses" : [
                                "0088", "0099"
                              ]
                            }
                          ]
                        }
                                
                        """
            );
        }
    }
}
