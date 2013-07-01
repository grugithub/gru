package com.github.gru;

import com.github.antlrjavaparser.api.CompilationUnit;
import com.github.antlrjavaparser.api.body.TypeDeclaration;
import com.github.antlrjavaparser.api.expr.AnnotationExpr;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    @Override
    public void statusUnknown(Path path, CompilationUnit compilationUnit) {

    }

    protected boolean hasClassAnnotation(CompilationUnit compilationUnit, String annotationName) {
        for (TypeDeclaration typeDeclaration : compilationUnit.getTypes()) {
            for (AnnotationExpr annotationExpr : typeDeclaration.getAnnotations()) {
                if (annotationExpr.getName().toString().equals(annotationName)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean hasPackageAnnotation(CompilationUnit compilationUnit, String annotationName) {
        if (compilationUnit.getPackage() != null) {
            for (AnnotationExpr annotationExpr : compilationUnit.getPackage().getAnnotations()) {
                if (annotationExpr.getName().toString().equals(annotationName)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void createFileFromString(String fileNameAndPath, String fileContents) {

        Path path = new File((fileNameAndPath)).toPath();

        Charset charset = Charset.forName("US-ASCII");

        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(path, charset, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            writer.write(fileContents, 0, fileContents.length());
            System.out.println("Created file: " + fileNameAndPath);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    // Ignore
                    ex.printStackTrace();
                }
            }
        }
    }

    protected void createRelatedFile(Path hostFile, String classifier, String extension, String fileContents) {
        createFileFromString(getRelatedFileName(hostFile, classifier, extension), fileContents);
    }

    protected void deleteRelatedFile(Path hostFile, String classifier, String extension) {
        deleteFile(getRelatedFileName(hostFile, classifier, extension));
    }

    private String getRelatedFileName(Path hostFile, String classifier, String extension) {
        // Remove the extension
        String fileName = FilenameUtils.removeExtension(hostFile.toString());

        fileName = fileName + "_Gru_" + classifier + "." + extension;

        return fileName;
    }

    protected void deleteFile(String fileNameAndPath) {
        Path path = new File((fileNameAndPath)).toPath();

        try {
            if (Files.exists(path)) {
                Files.deleteIfExists(path);
                System.out.println("Deleted file: " + fileNameAndPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
