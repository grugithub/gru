package com.github.gru;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        new WatchDir(dir, true, new FileChangedEvent(currentWorkingDirectory)).processEvents();
    }
}
