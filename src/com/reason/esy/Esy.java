package com.reason.esy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

import static com.reason.esy.EsyConstants.ESY_EXECUTABLE_NAME;

public class Esy {

    private Esy() {}

    public static Optional<VirtualFile> findEsyExecutable(@NotNull Project project) {
            String systemPath = System.getenv("PATH");
            Optional<Path> esyExecutablePath = Platform.findExecutableInPath(ESY_EXECUTABLE_NAME, systemPath);
            if (!esyExecutablePath.isPresent()) {
            // @TODO pick up from here...
            }
            return esyExecutablePath;
    }
}
