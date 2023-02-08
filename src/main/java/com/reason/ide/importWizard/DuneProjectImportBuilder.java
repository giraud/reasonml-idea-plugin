package com.reason.ide.importWizard;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.packaging.artifacts.*;
import com.intellij.projectImport.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class DuneProjectImportBuilder extends ProjectImportBuilder<Module> {
    @Override
    public @NotNull String getName() {
        return "Dune (OCaml)";
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.DUNE;
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return true;
    }

    @Override
    public @Nullable List<Module> getList() {
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

    @Override
    public @Nullable List<Module> commit(@NotNull Project project, @Nullable ModifiableModuleModel moduleModel, ModulesProvider modulesProvider, ModifiableArtifactModel artifactModel) {
        return new ArrayList<>();
    }
}
