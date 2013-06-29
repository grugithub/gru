package com.github.gru;

import com.github.antlrjavaparser.api.CompilationUnit;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public class GroovyClassThing implements GruScriptInterface {

    private List<GruScriptInterface> plugins = new ArrayList<GruScriptInterface>();

    public void loadPlugins(String directory) {

        System.out.println("Loading Gru plugins...");

        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        final List<Path> pluginsToLoad = new ArrayList<Path>();

        Path gruFolder = new File(directory + File.separator + "gru").toPath();

        if (!gruFolder.toFile().exists()) {
            System.out.println("Plugin directory not found in project: " + gruFolder);
            return;
        }

        try {
            Files.walkFileTree(gruFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    if (file.toString().endsWith(".groovy")) {
                        pluginsToLoad.add(file);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for (Path path : pluginsToLoad) {
            try {
                Class groovyClass = loader.parseClass(path.toFile());
                plugins.add((GruScriptInterface)groovyClass.newInstance());
                System.out.println("Loaded plugin: " + path.toString());
            } catch (InstantiationException e) {
                System.err.println("Unable to instantiate plugin script: " + path.toString());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println("Unable to instantiate plugin script: " + path.toString());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Unable to parse plugin script: " + path.toString());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        System.out.println("Plugins loaded");
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

    @Override
    public void statusUnknown(Path path, CompilationUnit compilationUnit) {
        // Call all plugins
        for (GruScriptInterface gruScriptInterface : plugins) {
            gruScriptInterface.statusUnknown(path, compilationUnit);
        }
    }
}
