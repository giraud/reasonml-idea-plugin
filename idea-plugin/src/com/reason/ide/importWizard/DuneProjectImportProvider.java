package com.reason.ide.importWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.projectImport.ProjectImportProvider;
import org.jetbrains.annotations.NotNull;

/** A {@link ProjectImportProvider} with ability to import Dune projects. */
public class DuneProjectImportProvider extends ProjectImportProvider {

  @NotNull
  @Override
  protected ProjectImportBuilder doGetBuilder() {
    return new DuneProjectImportBuilder();
  }

  @NotNull
  public ModuleWizardStep @NotNull [] createSteps(@NotNull WizardContext context) {
    return new ModuleWizardStep[] {new OclProjectJdkWizardStep(context)};
  }

  @Override
  protected boolean canImportFromFile(@NotNull VirtualFile file) {
    return "dune-project".equals(file.getName());
  }

  @NotNull
  @Override
  public String getPathToBeImported(@NotNull VirtualFile file) {
    return file.getPath();
  }

  @NotNull
  @Override
  public String getFileSample() {
    return "<b>Dune</b> project file (dune-project)";
  }
}
