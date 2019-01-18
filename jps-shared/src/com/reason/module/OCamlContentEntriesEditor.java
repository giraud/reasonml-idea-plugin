package com.reason.module;

import com.intellij.openapi.roots.ui.configuration.CommonContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.jps.model.java.JavaSourceRootType;

public class OCamlContentEntriesEditor extends CommonContentEntriesEditor {
    public OCamlContentEntriesEditor(String moduleName, ModuleConfigurationState state) {
        super(moduleName, state, JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE);
    }
}
