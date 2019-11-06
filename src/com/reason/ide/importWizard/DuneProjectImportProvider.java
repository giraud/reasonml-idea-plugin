package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportProvider;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ProjectImportProvider} with ability to import Dune projects.
 */
public class DuneProjectImportProvider extends ProjectImportProvider {

    private static final Key<ProjectJdkStep> PROJECT_JDK_STEP_KEY = Key.create("ProjectJdkStep");

    @NotNull
    @Override
    protected ProjectImportBuilder doGetBuilder() {
        return new DuneProjectImportBuilder();
    }

    @NotNull
    public ModuleWizardStep[] createSteps(@NotNull WizardContext context) {
        return new ModuleWizardStep[]{
                new OclProjectJdkWizardStep(context)};
    }

    @Override
    protected boolean canImportFromFile(@NotNull VirtualFile file) {
        System.out.println("canImport? " + file);
        return "dune-project".equals(file.getName());
    }

    @NotNull
    @Override
    public String getPathToBeImported(@NotNull VirtualFile file) {
        System.out.println("DuneProjectImportProvider.getPathToBeImported / file = " + file);
        return file.getPath();
    }

    @NotNull
    @Override
    public String getFileSample() {
        return "<b>Dune</b> configuration file";
    }

}
