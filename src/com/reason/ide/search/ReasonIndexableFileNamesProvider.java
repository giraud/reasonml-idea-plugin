package com.reason.ide.search;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.javascript.modules.NodeModulesIndexableFileNamesProvider;

public class ReasonIndexableFileNamesProvider extends NodeModulesIndexableFileNamesProvider {

    private static final List<String> EXTENSIONS = new ArrayList<>(4);
    private static final List<String> FILES = new ArrayList<>(1);

    static {
        EXTENSIONS.add("ml");
        EXTENSIONS.add("mli");
        EXTENSIONS.add("re");
        EXTENSIONS.add("rei");
        FILES.add("bsconfig.json");
    }

    @NotNull
    @Override
    protected List<String> getIndexableFileNames(@NotNull DependencyKind kind) {
        return FILES;
    }

    @NotNull
    @Override
    protected List<String> getIndexableExtensions(@NotNull DependencyKind kind) {
        return EXTENSIONS;
    }
}
