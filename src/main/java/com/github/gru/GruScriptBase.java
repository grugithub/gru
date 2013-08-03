package com.github.gru;

import com.github.antlrjavaparser.api.CompilationUnit;
import com.github.antlrjavaparser.api.body.BodyDeclaration;
import com.github.antlrjavaparser.api.body.TypeDeclaration;
import com.github.antlrjavaparser.api.expr.AnnotationExpr;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
            if (hasClassAnnotation((BodyDeclaration)typeDeclaration, annotationName)) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasClassAnnotation(BodyDeclaration bodyDeclaration, String annotationName) {
        for (AnnotationExpr annotationExpr : bodyDeclaration.getAnnotations()) {
            if (annotationExpr.getName().toString().equals(annotationName)) {
                return true;
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

        fileContents = "/* GRU_VERSION(" + GruVersion.GRU_VERSION.toString() + ") */" + System.lineSeparator() + fileContents;

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

    /**
     * Used to test if a generated file was generated from a newer version of Gru
     *
     * @return
     */
    protected boolean isSafeToOverwriteFile(Path hostFile, String classifier, String extension) {
        File generatedFile = new File(getRelatedFileName(hostFile, classifier, extension));

        // If the file doesn't exist yet, then it's safe to overwrite
        if (!generatedFile.exists()) {
            return true;
        }

        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(new FileReader(generatedFile));
            String versionLine = fileReader.readLine();

            // If the version string is not found, it is safe to overwrite
            if (versionLine == null || versionLine.length() < 11) {
                return true;
            }

            String gruVersionString = null;
            try {
                Pattern regex = Pattern.compile("GRU_VERSION\\(([0-9.?]+)\\)");
                Matcher regexMatcher = regex.matcher(versionLine);
                if (regexMatcher.find()) {
                    gruVersionString = regexMatcher.group(1);
                }
            } catch (PatternSyntaxException ex) {
                // My syntax is perfect
                ex.printStackTrace();
            }

            // Version string not found
            if (gruVersionString == null || gruVersionString.length() == 0) {
                return true;
            }

            GruVersion gruVersion = new GruVersion(gruVersionString);
            if (GruVersion.GRU_VERSION.compareTo(gruVersion) >= 0) {
                return true;
            } else {
                System.err.println("*********************************************************************");
                System.err.println("** WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING **");
                System.err.println("*********************************************************************");
                System.err.println("* This file was created with a newer version of Gru.                *");
                System.err.println("* Please download the latest version of Gru:                        *");
                System.err.println("*                                                                   *");
                System.err.println("* https://github.com/grugithub/gru/releases                         *");
                System.err.println("*********************************************************************");
                return false;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to determine if it's safe to overwrite file: " + hostFile.toString(), ex);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {

                }
            }
        }
    }

    protected String getRelatedFileName(Path hostFile, String classifier, String extension) {
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
