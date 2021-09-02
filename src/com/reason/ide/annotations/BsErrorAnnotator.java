package com.reason.ide.annotations;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.bs.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class BsErrorAnnotator extends ORErrorAnnotator {
    @Override
    @Nullable VirtualFile getContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return BsPlatform.findContentRoot(project, sourceFile).orElse(null);
    }

    @Override
    @NotNull Ninja readNinja(@NotNull Project project, @NotNull VirtualFile contentRoot) {
        return project.getService(BsCompiler.class).readNinjaBuild(contentRoot);
    }

    @Override
    @NotNull List<OutputInfo> compile(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull ArrayList<String> arguments, @NotNull VirtualFile workDir) {
        return new BscProcess(project).exec(sourceFile, workDir, arguments);
    }
}
