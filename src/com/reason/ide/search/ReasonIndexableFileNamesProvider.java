package com.reason.ide.search;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.javascript.modules.NodeModulesIndexableFileNamesProvider;

public class ReasonIndexableFileNamesProvider extends NodeModulesIndexableFileNamesProvider {

    private static final List<String> EXTENSIONS = new ArrayList<>(4);

    static {
        EXTENSIONS.add("ml");
        EXTENSIONS.add("mli");
        EXTENSIONS.add("re");
        EXTENSIONS.add("rei");
    }

    @NotNull
    @Override
    protected List<String> getIndexableExtensions(@NotNull DependencyKind kind) {
        return EXTENSIONS;
    }
}
