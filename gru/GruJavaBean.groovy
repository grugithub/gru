import com.github.antlrjavaparser.api.CompilationUnit
import com.github.antlrjavaparser.api.body.BodyDeclaration
import com.github.antlrjavaparser.api.body.ClassOrInterfaceDeclaration
import com.github.antlrjavaparser.api.body.FieldDeclaration
import com.github.antlrjavaparser.api.body.MethodDeclaration
import com.github.antlrjavaparser.api.body.ModifierSet
import com.github.antlrjavaparser.api.body.TypeDeclaration
import com.github.antlrjavaparser.api.body.VariableDeclarator
import com.github.gru.GruScriptBase
import org.apache.commons.lang.WordUtils

import java.nio.file.Path
import java.nio.file.WatchEvent

public class GruJavaBean extends GruScriptBase {
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
        generateFile(path, compilationUnit);
    }

    void generateFile(Path path, CompilationUnit compilationUnit) {
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

        ClassOrInterfaceDeclaration mainClass = (ClassOrInterfaceDeclaration)mainType;

        List<FieldDeclaration> fieldsToUse = new ArrayList<FieldDeclaration>();
        for (BodyDeclaration bodyDeclaration : mainClass.getMembers()) {
            if (bodyDeclaration instanceof FieldDeclaration) {
                fieldsToUse.add((FieldDeclaration)bodyDeclaration);
            }
        }

        Map<String, String> nameTypeFields = new TreeMap<String, String>();

        StringBuffer gettersAndSetters = new StringBuffer();

        for (FieldDeclaration fieldDeclaration : fieldsToUse) {

            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
                String arrayDeclare = "[]".multiply(variableDeclarator.getId().getArrayCount());

                if (!hasGetter(variableDeclarator, mainClass)) {
                    gettersAndSetters.append(
                            "    public " + fieldDeclaration.getType().toString() + arrayDeclare + " " +
                            mainClass.getName() + ".get" + WordUtils.capitalize(variableDeclarator.getId().getName()) +
                            "() {\n" +
                            "        return this." + variableDeclarator.getId().getName() + ";\n" +
                            "    }\n"
                    );
                }

                if (!hasSetter(variableDeclarator, mainClass)) {
                    gettersAndSetters.append(
                            "    public void " + mainClass.getName() + ".set" + WordUtils.capitalize(variableDeclarator.getId().getName()) +
                                    "(" + fieldDeclaration.getType().toString() + arrayDeclare + " " + variableDeclarator.getId().getName() + ") {\n" +
                                    "        this." + variableDeclarator.getId().getName() + " = " + variableDeclarator.getId().getName() + ";\n" +
                                    "    }\n"
                    );
                }

            }
        }

        String content =
            compilationUnit.getPackage().toString() + "\n" +
                "\n" +
                "privileged aspect " + mainClass.getName() + "_Gru_JavaBean {\n\n" +
                    gettersAndSetters.toString() +
                "}";

        createRelatedFile(path, "JavaBean", "aj", content)
    }

    private boolean hasGetter(VariableDeclarator variableDeclarator, ClassOrInterfaceDeclaration mainClass) {
        String getterName = "get" + WordUtils.capitalize(variableDeclarator.getId().getName());
        return hasMethodName(getterName, variableDeclarator, mainClass);
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
}
