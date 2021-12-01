package jpsplugin.com.reason.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.*;
import com.intellij.openapi.roots.ui.configuration.*;
import org.jetbrains.annotations.*;

public class OCamlDefaultModuleEditorsProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor @NotNull [] createEditors(@NotNull ModuleConfigurationState state) {
        Module module = state.getCurrentRootModel().getModule();
        if (ModuleType.get(module) instanceof OCamlModuleType) {
            return new ModuleConfigurationEditor[]{
                    new OCamlContentEntriesEditor(module.getName(), state)
            };
        }

        return ModuleConfigurationEditor.EMPTY;
    }
}
