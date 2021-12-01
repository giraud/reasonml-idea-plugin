package com.reason.ide.importWizard;

import com.intellij.openapi.application.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ex.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.vfs.*;
import com.intellij.packaging.artifacts.*;
import com.intellij.projectImport.*;
import icons.*;
import jpsplugin.com.reason.module.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

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
    public void setList(List<Module> list) {
    }

    @Override
    public void setOpenProjectSettingsAfter(boolean on) {
    }

    @Nullable
    @Override
    public List<Module> commit(@NotNull Project project, @Nullable ModifiableModuleModel moduleModel, ModulesProvider modulesProvider, ModifiableArtifactModel artifactModel) {
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
                    Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                        @Override
                        public @NotNull FileVisitResult visitFile(@NotNull Path path, BasicFileAttributes basicFileAttributes) {
                            if ("dune".equals(path.getFileName().toString())) {
                                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path.toString());
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
