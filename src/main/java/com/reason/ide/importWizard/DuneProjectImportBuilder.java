package com.reason.ide.importWizard;

import com.intellij.openapi.application.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.vfs.*;
import com.intellij.packaging.artifacts.*;
import com.intellij.projectImport.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.settings.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

/**
 * Builder used when using menu File | New | Project from existing sources
 */
public class DuneProjectImportBuilder extends ProjectImportBuilder<ImportedDuneBuild> {
    private VirtualFile myProjectRoot = null;
    private List<ImportedDuneBuild> myFoundDuneBuilds = new ArrayList<>();
    private OpamSettings myOpamSettings;

    @NonNls
    @Override
    public @NotNull String getName() {
        return "Dune Import Builder";
    }

    @Override
    public Icon getIcon() {
        return ORIcons.DUNE;
    }

    @Override
    public void setList(@Nullable List<ImportedDuneBuild> selectedDuneApps) {
        if (selectedDuneApps != null) {
            myFoundDuneBuilds = selectedDuneApps;
        }
    }

    @Override
    public boolean isMarked(ImportedDuneBuild importedDuneBuild) {
        return false;
    }

    @Override
    public void setOpenProjectSettingsAfter(boolean on) {
    }

    @Override
    public void cleanup() {
        myProjectRoot = null;
        myFoundDuneBuilds = new ArrayList<>();
    }


    public void setOpamSettings(OpamSettings settings) {
        myOpamSettings = settings;
    }

    public void setProjectRoot(@NotNull final VirtualFile projectRoot) {
        if (projectRoot.equals(myProjectRoot)) {
            return;
        }

        myProjectRoot = projectRoot;

        ProgressManager.getInstance().run(new Task.Modal(getCurrentProject(), "Scanning Dune Projects", true) {
            public void run(@NotNull final ProgressIndicator indicator) {
                myProjectRoot.refresh(false, true);

                VfsUtilCore.visitChildrenRecursively(myProjectRoot, new VirtualFileVisitor<Void>() {
                    @Override
                    public boolean visitFile(@NotNull VirtualFile file) {
                        indicator.checkCanceled();

                        if (file.isDirectory()) {
                            if (".git".equals(file.getName())) {
                                return false;
                            }
                            indicator.setText2(file.getPath());
                        } else if (file.getName().equalsIgnoreCase(DuneExternalConstants.BUILD_FILE)) {
                            myFoundDuneBuilds.add(new ImportedDuneBuild(file));
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public @Nullable List<Module> commit(Project project, ModifiableModuleModel moduleModel, ModulesProvider modulesProvider, ModifiableArtifactModel artifactModel) {
        List<Module> createdModules = new ArrayList<>();

        ModifiableModuleModel obtainedModuleModel = moduleModel == null ? ModuleManager.getInstance(project).getModifiableModel() : moduleModel;

        String ideaModuleDirPath = myProjectRoot.getCanonicalPath();
        if (ideaModuleDirPath == null) {
            return createdModules;
        }
        Module module = obtainedModuleModel.newModule(ideaModuleDirPath, JavaModuleType.getModuleType().getId());

        createdModules.add(module);

        ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();

        ContentEntry content = rootModel.addContentEntry(myProjectRoot);
        for (ImportedDuneBuild importedDuneBuild : myFoundDuneBuilds) {
            boolean isTest = false;

            PsiFile file = PsiManagerEx.getInstanceEx(project).getFileManager().findFile(importedDuneBuild.getBuild());
            if (file instanceof DuneFile duneFile) {
                Collection<RPsiDuneStanza> stanzas = PsiTreeUtil.findChildrenOfType(duneFile, RPsiDuneStanza.class);
                Optional<String> hasTest = stanzas.stream().map(RPsiDuneStanza::getName).filter(name -> name != null && name.startsWith("test")).findFirst();
                isTest = hasTest.isPresent();
            }

            content.addSourceFolder(importedDuneBuild.getRoot(), isTest);
        }

        // Commit project structure.
        ApplicationManager.getApplication().runWriteAction(() -> {
            rootModel.commit();
            obtainedModuleModel.commit();

            if (myOpamSettings != null) {
                ORSettings settings = project.getService(ORSettings.class);
                settings.setOpamLocation(myOpamSettings.getOpamLocation());
                settings.setSwitchName(myOpamSettings.getOpamSwitch());
                settings.setIsWsl(myOpamSettings.isWsl());
                settings.setCygwinBash(myOpamSettings.getCygwinBash());
            }
        });

        return createdModules;
    }

    public interface OpamSettings {
        String getOpamLocation();

        String getOpamSwitch();

        boolean isWsl();

        String getCygwinBash();
    }
}
