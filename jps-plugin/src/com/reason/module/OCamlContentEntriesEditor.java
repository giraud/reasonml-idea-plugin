package com.reason.module;

import com.intellij.openapi.roots.ui.configuration.CommonContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;

class OCamlContentEntriesEditor extends CommonContentEntriesEditor {
  OCamlContentEntriesEditor(@NotNull String moduleName, @NotNull ModuleConfigurationState state) {
    super(
        moduleName,
        state,
        JavaSourceRootType.SOURCE,
        JavaSourceRootType.TEST_SOURCE,
        OCamlBinaryRootType.BINARY);
  }
}
