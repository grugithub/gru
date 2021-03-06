import com.github.antlrjavaparser.api.CompilationUnit
import com.github.antlrjavaparser.api.body.BodyDeclaration
import com.github.antlrjavaparser.api.body.ClassOrInterfaceDeclaration
import com.github.antlrjavaparser.api.body.FieldDeclaration
import com.github.antlrjavaparser.api.body.MethodDeclaration
import com.github.antlrjavaparser.api.body.ModifierSet
import com.github.antlrjavaparser.api.body.TypeDeclaration
import com.github.antlrjavaparser.api.body.VariableDeclarator
import com.github.antlrjavaparser.api.type.PrimitiveType
import com.github.gru.GruScriptBase
import org.apache.commons.lang3.text.WordUtils

import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

public class GruJavaBean extends GruScriptBase {

    /**
     * %1\$s - returnType
     * %2\$s - arrayDeclare
     * %3\$s - className
     * %4\$s - getterPrefix
     * %5\$s - getterName
     * %6\$s - fieldName
     * %7\$s - line end
     */
    private static final GETTER_FORMAT =
        "    public %1\$s%2\$s %3\$s.%4\$s%5\$s() {%7\$s" +
        "        return this.%6\$s;%7\$s" +
        "    }%7\$s%7\$s";

    /**
     * %1\$s - className
     * %2\$s - setterName
     * %3\$s - type
     * %4\$s - arrayDeclare
     * %5\$s - fieldName
     * %6\$s - line end
     */
    private static final SETTER_FORMAT =
        "    public void %1\$s.set%2\$s(%3\$s%4\$s %5\$s) {%6\$s" +
        "        this.%5\$s = %5\$s;%6\$s" +
        "    }%6\$s%6\$s";

    /**
     * %1\$s - packageName
     * %2\$s - importStatements
     * %3\$s - className
     * %4\$s - gettersAndSetters
     * %5\$s - line end
     */
    private static final FILE_FORMAT =
        "%1\$s%5\$s" +
        "%2\$s%5\$s" +
        "/*%5\$s" +
        " * DO NOT ALTER THIS FILE.%5\$s" +
        " * This file is automatically generated by Gru.%5\$s" +
        " */%5\$s" +
        "privileged aspect %3\$s_Gru_JavaBean {%5\$s%5\$s" +
        "%4\$s" +
        "}";

    @Override
    void fileChanged(WatchEvent.Kind<?> eventType, Path path, CompilationUnit compilationUnit) {
        generateFile(path, compilationUnit);
    }

    @Override
    void fileDeleted(WatchEvent.Kind<?> eventType, Path path) {
        // The host file was deleted, so delete the generated files.
        deleteRelatedFile(path, "JavaBean", "aj");
    }

    @Override
    void statusUnknown(Path path, CompilationUnit compilationUnit) {
        boolean skipFile = false;

        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime sourceFileTime = attrs.lastModifiedTime();

            String generatedFileName = getRelatedFileName(path, "JavaBean", "aj");

            File generatedFile = new File(generatedFileName);

            if (generatedFile.exists()) {
                BasicFileAttributes generatedFileAttrs = Files.readAttributes(generatedFile.toPath(), BasicFileAttributes.class);
                FileTime generatedFileTime = generatedFileAttrs.lastModifiedTime();

                if (sourceFileTime.compareTo(generatedFileTime) <= 0) {
                    skipFile = true;
                }
            }

        } catch (IOException ex) {
            // Leave skip false
        }

        if (!skipFile) {
            generateFile(path, compilationUnit);
        } else {
            System.out.println("Skipping unmodified file: " + path.toString());
        }
    }

    void generateFile(Path path, CompilationUnit compilationUnit) {

        if (!isSafeToOverwriteFile(path, "JavaBean", "aj")) {
            return;
        }

        if (!hasClassAnnotation(compilationUnit, "GruJavaBean")) {
            // Delete any left over files
            deleteRelatedFile(path, "JavaBean", "aj");

            return;
        }

        // Grab a list of all private fields.
        TypeDeclaration mainType = null;
        for (TypeDeclaration type : compilationUnit.types) {
            if (ModifierSet.isPublic(type.getModifiers())) {
                mainType = type;
                break;
            }
        }

        if (mainType == null) {
            return;
        }

        if (!(mainType instanceof ClassOrInterfaceDeclaration)) {
            return;
        }

        StringBuffer gettersAndSetters = new StringBuffer();

        ClassOrInterfaceDeclaration mainClass = (ClassOrInterfaceDeclaration)mainType;
        visitMember(mainType, mainClass.getName(), gettersAndSetters);

        // Print out the imports of the file
        String importString = "";
        gatherImports(compilationUnit).each {importString += "${it}"};

        String packageName = "";
        if (compilationUnit.getPackage() != null) {
            packageName = compilationUnit.getPackage().toString().replaceAll("[\n\r]", "") +
                System.lineSeparator();
        }

        String content = String.format(FILE_FORMAT,
            packageName,
            importString,
            mainClass.getName(),
            gettersAndSetters.toString(),
            System.lineSeparator()
        );

        createRelatedFile(path, "JavaBean", "aj", content);
    }

    private StringBuffer generateGettersAndSettersForMember(ClassOrInterfaceDeclaration mainClass, String memberPrefix) {
        List<FieldDeclaration> fieldsToUse = new ArrayList<FieldDeclaration>();
        for (BodyDeclaration bodyDeclaration : mainClass.getMembers()) {
            if (bodyDeclaration instanceof FieldDeclaration) {
                fieldsToUse.add((FieldDeclaration) bodyDeclaration);
            }
        }

        Map<String, String> nameTypeFields = new TreeMap<String, String>();

        StringBuffer gettersAndSetters = new StringBuffer();

        for (FieldDeclaration fieldDeclaration : fieldsToUse) {

            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
                String arrayDeclare = "[]".multiply(variableDeclarator.getId().getArrayCount());

                String getterPrefix = "get";
                if (fieldDeclaration.getType() instanceof PrimitiveType) {
                    PrimitiveType primitiveType = (PrimitiveType) fieldDeclaration.getType();
                    if (primitiveType.getType().equals(PrimitiveType.Primitive.Boolean)) {
                        getterPrefix = "is";
                    }
                }

                if (!hasGetter(variableDeclarator, mainClass)) {

                    gettersAndSetters.append(
                            String.format(GETTER_FORMAT,
                                    fieldDeclaration.getType().toString(),
                                    arrayDeclare,
                                    memberPrefix,
                                    getterPrefix,
                                    WordUtils.capitalize(variableDeclarator.getId().getName()),
                                    variableDeclarator.getId().getName(),
                                    System.lineSeparator()
                            )
                    );
                }

                if (!hasSetter(variableDeclarator, mainClass)) {

                    // If the field is not marked final, create the setter
                    if (!ModifierSet.hasModifier(fieldDeclaration.getModifiers(), ModifierSet.FINAL)) {
                        gettersAndSetters.append(
                                String.format(SETTER_FORMAT,
                                        memberPrefix,
                                        WordUtils.capitalize(variableDeclarator.getId().getName()),
                                        fieldDeclaration.getType().toString(),
                                        arrayDeclare,
                                        variableDeclarator.getId().getName(),
                                        System.lineSeparator()
                                )
                        );
                    }
                }
            }
        }

        return gettersAndSetters;
    }

    private boolean hasGetter(VariableDeclarator variableDeclarator, ClassOrInterfaceDeclaration mainClass) {
        String getterName = "get" + WordUtils.capitalize(variableDeclarator.getId().getName());
        String getterIsName = "is" + WordUtils.capitalize(variableDeclarator.getId().getName());

        if (hasMethodName(getterName, variableDeclarator, mainClass)) {
            return true;
        } else if (hasMethodName(getterIsName, variableDeclarator, mainClass)) {
            return true;
        }

        return false;
    }

    private boolean hasSetter(VariableDeclarator variableDeclarator, ClassOrInterfaceDeclaration mainClass) {
        String setterName = "set" + WordUtils.capitalize(variableDeclarator.getId().getName());
        return hasMethodName(setterName, variableDeclarator, mainClass);
    }

    private boolean hasMethodName(String methodName, VariableDeclarator variableDeclarator, ClassOrInterfaceDeclaration mainClass) {
        for (BodyDeclaration bodyDeclaration : mainClass.getMembers()) {
            if (bodyDeclaration instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclaration;

                if (methodDeclaration.getName().equals(methodName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<String> gatherImports(CompilationUnit compilationUnit) {
        List<String> importList = new ArrayList<>();
        compilationUnit.getImports().each() { importDeclaration ->
            String importString = importDeclaration.toString().replaceAll("[\n\r]", "")
            importList.add(importString + System.lineSeparator())
        };
        return importList;
    }

    private void visitMember(ClassOrInterfaceDeclaration member, String memberPrefix, StringBuffer gettersAndSetters) {

        if (hasClassAnnotation(member, "GruJavaBean")) {
            gettersAndSetters.append(generateGettersAndSettersForMember(member, memberPrefix));
        }

        for (BodyDeclaration bodyDeclaration : member.getMembers()) {
            if (bodyDeclaration instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration)bodyDeclaration;
                visitMember((ClassOrInterfaceDeclaration)bodyDeclaration, memberPrefix + "." + classOrInterfaceDeclaration.getName(), gettersAndSetters);
            }
        }
    }
}
