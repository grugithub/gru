package com.github.gru;

import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.api.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public class FileChangedEvent {

    private GroovyClassThing groovyClassThing = new GroovyClassThing();

    public void loadPlugins(String currentWorkingDirectory) {
        groovyClassThing.loadPlugins(currentWorkingDirectory);
    }

    public void fileChanged(WatchEvent.Kind<?> eventType, Path path) {

        // We're only parsing Java files for now
        if (!path.toString().endsWith(".java")) {
            return;
        }

        if (eventType == ENTRY_CREATE || eventType == ENTRY_MODIFY) {

            int attempt = 0;
            boolean readSuccess = false;

            do {
                try {
                    CompilationUnit compilationUnit = JavaParser.parse(path.toFile());
                    groovyClassThing.fileChanged(eventType, path, compilationUnit);
                    readSuccess = true;
                } catch (FileNotFoundException ex) {

                    // Since this is a create or modify event,
                    // We really shouldn't get a FileNotFoundException
                    // but they are popping up anyway.

                    // Attempt to read the file 3 times

                    attempt++;

                    try {
                        Thread.sleep(100);
                    } catch (Exception ex2) {
                        // Ignore (We'll survive if the thread doesn't sleep)
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } while (attempt < 3 && !readSuccess);

            if (!readSuccess) {
                System.err.println("Failed to read file: " + path.toString());
            }

        } else if (eventType == ENTRY_DELETE) {
            groovyClassThing.fileDeleted(eventType, path);
            // Cleanup files that used to belong to this file
        }
    }

    public void initialize(String currentWorkingDirectory) {
        Path gruFolder = new File(currentWorkingDirectory).toPath();

        try {
            Files.walkFileTree(gruFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    if (file.toString().endsWith(".java")) {

                        CompilationUnit compilationUnit = JavaParser.parse(file.toFile());
                        groovyClassThing.statusUnknown(file, compilationUnit);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
