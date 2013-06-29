package com.github.gru;

import com.github.antlrjavaparser.api.CompilationUnit;
import com.github.antlrjavaparser.api.body.TypeDeclaration;
import com.github.antlrjavaparser.api.expr.AnnotationExpr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * User: mdehaan
 * Date: 6/28/13
 */
public abstract class GruScriptBase implements GruScriptInterface {

    @Override
    public void fileChanged(WatchEvent.Kind<?> eventType, Path path, CompilationUnit compilationUnit) {

    }

    @Override
    public void fileDeleted(WatchEvent.Kind<?> eventType, Path path) {

    }

    public boolean hasClassAnnotation(CompilationUnit compilationUnit, String annotationName) {
        for (TypeDeclaration typeDeclaration : compilationUnit.getTypes()) {
            for (AnnotationExpr annotationExpr : typeDeclaration.getAnnotations()) {
                if (annotationExpr.getName().toString().equals(annotationName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasPackageAnnotation(CompilationUnit compilationUnit, String annotationName) {
        if (compilationUnit.getPackage() != null) {
            for (AnnotationExpr annotationExpr : compilationUnit.getPackage().getAnnotations()) {
                if (annotationExpr.getName().toString().equals(annotationName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void createFileFromString(String fileNameAndPath, String fileContents) {

        Path path = new File((fileNameAndPath)).toPath();

        Charset charset = Charset.forName("US-ASCII");

        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(path, charset);
            writer.write(fileContents, 0, fileContents.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }
    }

    public void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
