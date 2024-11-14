package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.vfs.*;
import com.intellij.projectImport.*;
import org.jetbrains.annotations.*;

public class DuneProjectOpenProcessor extends ProjectOpenProcessorBase<DuneProjectImportBuilder> {
    @Override
    public @NotNull String[] getSupportedExtensions() {
        return new String[]{DuneExternalConstants.PROJECT_BUILD_FILE};
    }

    @Override
    public boolean doQuickImport(@NotNull VirtualFile configFile, @NotNull WizardContext wizardContext) {
        VirtualFile projectRoot = configFile.getParent();

        wizardContext.setProjectName(projectRoot.getName());
        getBuilder().setProjectRoot(projectRoot);

        return true;
    }

    @Override
    protected @NotNull DuneProjectImportBuilder doGetBuilder() {
        return ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(DuneProjectImportBuilder.class);
    }
}
