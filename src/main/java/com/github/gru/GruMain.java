package com.github.gru;

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
//@Deprecated
public class GruMain {
    public static void main(String[] args) throws Exception {
        String currentWorkingDirectory = System.getProperty("user.dir");

        //GroovyClassThing thing = new GroovyClassThing();
        //thing.test(currentWorkingDirectory);

        // Start listening for file changes
        Path dir = Paths.get(currentWorkingDirectory);

        FileChangedEvent fileChangedEvent = new FileChangedEvent(currentWorkingDirectory);

        // Initialize from an unknown status
        fileChangedEvent.initialize(currentWorkingDirectory);

        // Start the listener
        new WatchDir(dir, true, fileChangedEvent).processEvents();
    }
}
