package com.reason;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OCamlModuleType extends ModuleType<OCamlModuleBuilder> {
    private static final String ID = "ocamlModuleType";

    public OCamlModuleType() {
        super(ID);
    }

    public static final OCamlModuleType getInstance() {
        return (OCamlModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public OCamlModuleBuilder createModuleBuilder() {
        return new OCamlModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "OCaml";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ocaml";
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return Icons.OCL_MODULE;
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull OCamlModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }
}
