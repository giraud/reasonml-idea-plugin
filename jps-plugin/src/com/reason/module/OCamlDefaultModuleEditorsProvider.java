package com.reason.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.NotNull;

public class OCamlDefaultModuleEditorsProvider implements ModuleConfigurationEditorProvider {

    @NotNull
    @Override
    public ModuleConfigurationEditor[] createEditors(@NotNull ModuleConfigurationState state) {
        Module module = state.getRootModel().getModule();
        if (ModuleType.get(module) instanceof OCamlModuleType) {
            return new ModuleConfigurationEditor[]{
                    new OCamlContentEntriesEditor(module.getName(), state)
            };
        }

        return ModuleConfigurationEditor.EMPTY;
    }

}
