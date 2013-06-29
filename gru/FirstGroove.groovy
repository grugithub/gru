import com.github.antlrjavaparser.api.CompilationUnit
import com.github.gru.GruScriptBase

import java.nio.file.Path
import java.nio.file.WatchEvent

public class FirstGroove extends GruScriptBase {
    @Override
    void fileChanged(WatchEvent.Kind<?> eventType, Path path, CompilationUnit compilationUnit) {

        if (!hasClassAnnotation(compilationUnit, "Deprecated")) {
            // Delete any left over files
            deleteFile(new File((path.toString() + ".test")).toPath());

            return;
        }

        createFileFromString(path.toString() + ".test", "This is a test");
    }

    @Override
    void fileDeleted(WatchEvent.Kind<?> eventType, Path path) {

        // The host file was deleted, so delete the generated files.
        deleteFile(new File((path.toString() + ".test")).toPath());
    }
}
