package com.reason.ide.importWizard;

import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

public class ImportedDuneBuild {
    private final VirtualFile myBuild;

    public ImportedDuneBuild(@NotNull VirtualFile build) {
        myBuild = build;
    }

    @NotNull
    public VirtualFile getBuild() {
        return myBuild;
    }

    @NotNull
    public VirtualFile getRoot() {
        return myBuild.getParent();
    }
}
