package com.reason.comp;

import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

/**
 * When a content root file has been found, we can associate it to its corresponding compiler.
 * Use ORCompilerManager to instantiate this class.
 */
public class ORResolvedCompiler<C extends Compiler> {
    protected final C myCompiler;
    protected final VirtualFile myContentRootFile; // Compiler configuration file, like bsconfig.json or dune-project
    protected final VirtualFile myBinFile;

    public ORResolvedCompiler(@NotNull C compiler, @NotNull VirtualFile contentRootFile, @Nullable VirtualFile binFile) {
        myCompiler = compiler;
        myContentRootFile = contentRootFile;
        myBinFile = binFile;
    }

    public @NotNull Compiler.CompilerType getType() {
        return myCompiler.getType();
    }

    public void runDefault(@NotNull VirtualFile file, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        myCompiler.runDefault(file, onProcessTerminated);
    }

    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        return myCompiler.getFullVersion(file); // TODO use contentRoot / remove file param
    }

    public @Nullable VirtualFile getContentRoot() {
        return myContentRootFile.getParent();
    }

    public @NotNull String getPath() {
        return myBinFile == null ? "" : myBinFile.getPath();
    }
}
