import com.github.antlrjavaparser.api.CompilationUnit
import com.github.gru.GruScriptBase

import java.nio.file.Path
import java.nio.file.WatchEvent

public class FirstGroove extends GruScriptBase {
    @Override
    void fileChanged(WatchEvent.Kind<?> eventType, Path path, CompilationUnit compilationUnit) {
        generateFile(path, compilationUnit);

    }

    @Override
    void fileDeleted(WatchEvent.Kind<?> eventType, Path path) {
        // The host file was deleted, so delete the generated files.
        deleteRelatedFile(path, "Deprecated", "aj");
    }

    @Override
    void statusUnknown(Path path, CompilationUnit compilationUnit) {
        generateFile(path, compilationUnit);
    }

    void generateFile(Path path, CompilationUnit compilationUnit) {
        if (!hasClassAnnotation(compilationUnit, "Deprecated")) {
            // Delete any left over files
            deleteRelatedFile(path, "Deprecated", "aj");

            return;
        }

        createRelatedFile(path, "Deprecated", "aj", compilationUnit.getPackage().toString())
    }
}
