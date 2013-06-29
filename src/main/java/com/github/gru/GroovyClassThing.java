package com.github.gru;

import com.github.antlrjavaparser.api.CompilationUnit;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public class GroovyClassThing implements GruScriptInterface {

    private List<GruScriptInterface> plugins = new ArrayList<GruScriptInterface>();

    public void loadPlugins(String directory) {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = null;
        try {
            groovyClass = loader.parseClass(new File(directory + File.separator + "gru" + File.separator + "FirstGroove.groovy"));
            plugins.add((GruScriptInterface)groovyClass.newInstance());
        } catch (IOException e) {
            System.err.println("Unable to parse plugin script: " + directory + File.separator + "gru" + File.separator + "FirstGroove.groovy");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Unable to instantiate plugin script: " + directory + File.separator + "gru" + File.separator + "FirstGroove.groovy");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Unable to instantiate plugin script: " + directory + File.separator + "gru" + File.separator + "FirstGroove.groovy");
            e.printStackTrace();
        }
    }

    @Override
    public void fileChanged(WatchEvent.Kind<?> eventType, Path path, CompilationUnit compilationUnit) {
        // Call all plugins
        for (GruScriptInterface gruScriptInterface : plugins) {
            gruScriptInterface.fileChanged(eventType, path, compilationUnit);
        }
    }

    @Override
    public void fileDeleted(WatchEvent.Kind<?> eventType, Path path) {
        // Call all plugins
        for (GruScriptInterface gruScriptInterface : plugins) {
            gruScriptInterface.fileDeleted(eventType, path);
        }
    }
}
