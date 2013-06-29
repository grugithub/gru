package com.github.gru;

import com.github.antlrjavaparser.api.CompilationUnit;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public interface GruScriptInterface {
    void fileChanged(WatchEvent.Kind<?> eventType, Path path, CompilationUnit compilationUnit);

    void fileDeleted(WatchEvent.Kind<?> eventType, Path path);

    void statusUnknown(Path path, CompilationUnit compilationUnit);
}
