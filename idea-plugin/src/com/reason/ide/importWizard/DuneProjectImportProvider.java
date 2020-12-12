package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.vfs.*;
import com.intellij.projectImport.*;
import org.jetbrains.annotations.*;

/**
 * A {@link ProjectImportProvider} with ability to import Dune projects.
 */
public class DuneProjectImportProvider extends ProjectImportProvider {
    @Override
    protected @NotNull ProjectImportBuilder<Module> doGetBuilder() {
        return new DuneProjectImportBuilder();
    }

    @NotNull
    public ModuleWizardStep @NotNull [] createSteps(@NotNull WizardContext context) {
        return new ModuleWizardStep[]{new OclProjectJdkWizardStep(context)};
    }

    @Override
    protected boolean canImportFromFile(@NotNull VirtualFile file) {
        return "dune-project".equals(file.getName());
    }

    @Override
    public @NotNull String getPathToBeImported(@NotNull VirtualFile file) {
        return file.getPath();
    }

    @Override
    public @NotNull String getFileSample() {
        return "<b>Dune</b> project file (dune-project)";
    }
}
