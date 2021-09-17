package com.reason.comp;

import com.intellij.openapi.application.ex.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

import java.beans.*;

public class CompileOnSave implements PropertyChangeListener {
    private final @NotNull Project myProject;
    private final @NotNull VirtualFile myFile;

    public CompileOnSave(@NotNull Project project, @NotNull VirtualFile file) {
        myProject = project;
        myFile = file;
    }

    // If document is modified, but there is no new value, it means it has been saved to disk.
    // When document is saved, run the compiler !!
    @Override
    public void propertyChange(@NotNull PropertyChangeEvent evt) {
        if ("modified".equals(evt.getPropertyName()) && evt.getNewValue() == Boolean.FALSE) {
            ORCompilerManager compilerManager = myProject.getService(ORCompilerManager.class);
            ORResolvedCompiler compiler = compilerManager == null ? null : compilerManager.getCompiler(myFile);
            if (compiler != null) {
                // We invokeLater because compiler needs access to index files and can't do it in the event thread
                ApplicationManagerEx.getApplicationEx()
                        .invokeLater(() -> {
                            if (!myProject.isDisposed()) {
                                compiler.runDefault(myFile, null);
                            }
                        });
            }
        }
    }
}
