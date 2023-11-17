package com.reason.comp.bs;

import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import org.jetbrains.annotations.*;

public class BsResolvedCompiler extends ORResolvedCompiler<BsCompiler> {
    public BsResolvedCompiler(@NotNull BsCompiler compiler, @NotNull VirtualFile contentRootFile, @Nullable VirtualFile binFile) {
        super(compiler, contentRootFile, binFile);
    }

    public @Nullable Ninja readNinja() {
        return myCompiler.readNinjaBuild(getContentRoot());
    }
}
