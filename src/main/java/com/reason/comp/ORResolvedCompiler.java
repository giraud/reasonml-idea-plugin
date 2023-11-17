package com.reason.comp;

import com.intellij.openapi.vfs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

/**
 * When a content root file has been found, we can associate it to its corresponding compiler.
 * Use ORCompilerManager to instantiate this class.
 */
public class ORResolvedCompiler<C extends Compiler> {
    protected final C myCompiler;
    protected final VirtualFile myConfigFile; // Compiler configuration file, like bsconfig.json or dune-project
    protected final VirtualFile myBinFile;

    public ORResolvedCompiler(@NotNull C compiler, @NotNull VirtualFile configFile, @Nullable VirtualFile binFile) {
        myCompiler = compiler;
        myConfigFile = configFile;
        myBinFile = binFile;
    }

    public @NotNull Compiler.CompilerType getType() {
        return myCompiler.getType();
    }

    public void runDefault(@NotNull VirtualFile file, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        myCompiler.runDefault(file, onProcessTerminated);
    }

    public @NotNull String getFullVersion() {
        return myCompiler.getFullVersion(myConfigFile);
    }

    public @NotNull VirtualFile getConfigFile() {
        return myConfigFile;
    }

    public @Nullable VirtualFile getContentRoot() {
        return myConfigFile.getParent();
    }

    public @NotNull String getPath() {
        return myBinFile == null ? "" : myBinFile.getPath();
    }
}
