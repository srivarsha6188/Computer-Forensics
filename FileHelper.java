package org.example;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<String> getListOfInboxes() {
        FileFilter filter = f -> !f.isDirectory();
        File[] directories = new File("enron").listFiles(filter);
        List<String> names = new ArrayList<>();
        assert directories != null;
        for (File directory : directories) {
            names.add(directory.getName());
        }
        return names;
    }
}
