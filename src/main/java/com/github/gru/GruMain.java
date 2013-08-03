package com.github.gru;

import com.psclistens.gru.annotations.GruJavaBean;

import java.nio.file.Path;
import java.nio.file.Paths;

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

        IdleWatcher idleWatcher = new IdleWatcher(fileChangedEvent);
        Thread idleThread = new Thread(idleWatcher);
        idleThread.setDaemon(true);
        idleThread.start();

        // Start the listener
        new WatchDir(dir, true, idleWatcher).processEvents();
    }
}
