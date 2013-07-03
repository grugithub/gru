package com.github.gru;

import com.github.gru.annotations.GruJavaBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public class GruMain {

    public static void main(String[] args) throws Exception {
        String currentWorkingDirectory = System.getProperty("user.dir");

        // Start listening for file changes
        Path dir = Paths.get(currentWorkingDirectory);

        FileChangedEvent fileChangedEvent = new FileChangedEvent();
        fileChangedEvent.loadPlugins(currentWorkingDirectory);

        // Initialize from an unknown status
        fileChangedEvent.initialize(currentWorkingDirectory);

        // Start the listener
        new WatchDir(dir, true, fileChangedEvent).processEvents();
    }
}
