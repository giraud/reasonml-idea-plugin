package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.vfs.*;
import com.intellij.projectImport.*;
import org.jetbrains.annotations.*;

/**
 * An extension point for 'Import Module from Existing Sources' with ability to import Dune projects.
 */
public class DuneProjectImportProvider extends ProjectImportProvider {
    @Override
    protected ProjectImportBuilder<ImportedDuneBuild> doGetBuilder() {
        return ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(DuneProjectImportBuilder.class);
    }

    @Override
    public ModuleWizardStep[] createSteps(WizardContext context) {
        return new ModuleWizardStep[]{
                new DuneProjectRootStep(context)
        };
    }

    public static boolean canImport(@NotNull VirtualFile entry) {
        if (entry.isDirectory()) {
            entry = entry.findChild(DuneExternalConstants.PROJECT_BUILD_FILE);
        }
        return entry != null && !entry.isDirectory() && DuneExternalConstants.PROJECT_BUILD_FILE.equals(entry.getName());
    }


    @Override
    protected boolean canImportFromFile(@NotNull VirtualFile entry) {
        return canImport(entry);
    }

    @Override
    public @NotNull String getPathToBeImported(@NotNull VirtualFile file) {
        return file.isDirectory() ? file.getPath() : file.getParent().getPath();
    }

    @Override
    public @NotNull String getFileSample() {
        return "<b>Dune</b> project file (dune-project)";
    }
}
