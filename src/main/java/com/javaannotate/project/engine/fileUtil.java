package com.javaannotate.project.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class fileUtil {
    private String path;
    private String name;
    public static String fileContents;
    fileUtil(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String readFile() {
        try {
            File file = new File(this.path, this.name);
            FileReader fileReader = new FileReader(file);
            int fileContents_temp = 0;
            String fileContents = "";
            while ((fileContents_temp = fileReader.read()) != -1) {
                fileContents += (char) fileContents_temp;
            }
            fileReader.close();
            this.fileContents = fileContents;
            return fileContents;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
