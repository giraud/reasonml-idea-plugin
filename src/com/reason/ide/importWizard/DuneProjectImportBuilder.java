package com.reason.ide.importWizard;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import com.reason.module.OCamlModuleType;
import com.reason.sdk.OCamlSdkType;
import icons.ORIcons;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DuneProjectImportBuilder extends ProjectImportBuilder<Module> {

  @Nullable private Sdk m_sdk;

  @NotNull
  @Override
  public String getName() {
    return "Dune (OCaml)";
  }

  @Override
  public @NotNull Icon getIcon() {
    return ORIcons.DUNE;
  }

  @Override
  public boolean isSuitableSdkType(SdkTypeId sdkType) {
    return sdkType == OCamlSdkType.getInstance();
  }

  @Nullable
  @Override
  public List<Module> getList() {
    return null;
  }

  @Override
  public boolean isMarked(Module element) {
    return false;
  }

  @Override
  public void setList(List<Module> list) {}

  @Override
  public void setOpenProjectSettingsAfter(boolean on) {}

  @Nullable
  @Override
  public List<Module> commit(
      @NotNull Project project,
      @Nullable ModifiableModuleModel moduleModel,
      ModulesProvider modulesProvider,
      ModifiableArtifactModel artifactModel) {
    List<Module> createdModules = new ArrayList<>();

    String ideaModuleDirPath = project.getBasePath();
    if (ideaModuleDirPath != null) {
      String ideaModuleFile = ideaModuleDirPath + File.separator + project.getName() + ".iml";

      // Creating the OCaml module

      ModifiableModuleModel obtainedModuleModel =
          moduleModel != null
              ? moduleModel
              : ModuleManager.getInstance(project).getModifiableModel();

      Module module =
          obtainedModuleModel.newModule(ideaModuleFile, OCamlModuleType.getInstance().getId());
      createdModules.add(module);

      ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
      rootModel.inheritSdk();

      // Initialize the source and the test paths.

      VirtualFile rootDir = LocalFileSystem.getInstance().findFileByPath(ideaModuleDirPath);
      if (rootDir != null) {
        ContentEntry content = rootModel.addContentEntry(rootDir);

        try {
          Path rootPath = new File(ideaModuleDirPath).toPath();
          Files.walkFileTree(
              rootPath,
              new SimpleFileVisitor<Path>() {
                @NotNull
                @Override
                public FileVisitResult visitFile(
                    @NotNull Path path, BasicFileAttributes basicFileAttributes) {
                  if ("dune".equals(path.getFileName().toString())) {
                    VirtualFile file =
                        LocalFileSystem.getInstance().findFileByPath(path.toString());
                    VirtualFile dir = file == null ? null : file.getParent();
                    if (dir != null) {
                      content.addSourceFolder(dir, false);
                    }
                  }
                  return FileVisitResult.CONTINUE;
                }
              });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      // Commit project structure.
      Application application = ApplicationManager.getApplication();
      application.runWriteAction(
          () -> {
            obtainedModuleModel.commit();
            rootModel.commit();

            assert m_sdk != null;
            ProjectRootManagerEx.getInstanceEx(project).setProjectSdk(m_sdk);
            OCamlSdkType.reindexSourceRoots(m_sdk);
          });
    }

    return createdModules;
  }

  void setModuleSdk(@NotNull Sdk sdk) {
    m_sdk = sdk;
  }
}
