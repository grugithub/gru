package com.github.gru;

import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.api.CompilationUnit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public class FileChangedEvent {

    private GroovyClassThing groovyClassThing = new GroovyClassThing();

    public FileChangedEvent(String currentWorkingDirectory) {
        System.out.println("FileChangedEvent init");
        groovyClassThing.loadPlugins(currentWorkingDirectory);
        System.out.println("FileChangedEvent init done");
    }

    public void fileChanged(WatchEvent.Kind<?> eventType, Path path) {

        // We're only parsing Java files for now
        if (!path.toString().endsWith(".java")) {
            return;
        }

        if (eventType == ENTRY_CREATE || eventType == ENTRY_MODIFY) {
            try {
                CompilationUnit compilationUnit = JavaParser.parse(path.toFile());
                groovyClassThing.fileChanged(eventType, path, compilationUnit);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (eventType == ENTRY_DELETE) {
            groovyClassThing.fileDeleted(eventType, path);
            // Cleanup files that used to belong to this file
        }

    }
}
