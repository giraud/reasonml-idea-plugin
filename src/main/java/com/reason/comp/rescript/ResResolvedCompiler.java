package com.reason.comp.rescript;

import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import org.jetbrains.annotations.*;

public class ResResolvedCompiler extends ORResolvedCompiler<ResCompiler> {
    public ResResolvedCompiler(@NotNull ResCompiler compiler, @NotNull VirtualFile contentRootFile, @Nullable VirtualFile binFile) {
        super(compiler, contentRootFile, binFile);
    }

    public @Nullable Ninja readNinjaBuild() {
        return myCompiler.readNinjaBuild(myContentRootFile.getParent());
    }
}
