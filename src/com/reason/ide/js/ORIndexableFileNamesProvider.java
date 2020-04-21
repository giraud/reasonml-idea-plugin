package com.reason.ide.js;

import com.google.common.collect.ImmutableList;
import com.intellij.lang.javascript.modules.NodeModulesIndexableFileNamesProvider;
import com.reason.ide.files.BsConfigJsonFileType;
import com.reason.ide.files.DuneFileType;
import com.reason.ide.files.EsyPackageJsonFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ORIndexableFileNamesProvider extends NodeModulesIndexableFileNamesProvider {

    private static final List<String> EXTENSIONS = extensions();
    private static final List<String> FILES = files();

    private static List<String> extensions() {
        return ImmutableList.of("ml", "mli", "re", "rei");
    }

    private static List<String> files() {
        // configuration files need to be indexed to determine project types
        ArrayList<String> files = new ArrayList<>();
        files.add(BsConfigJsonFileType.getDefaultFilename());
        files.add(EsyPackageJsonFileType.getDefaultFilename());
        files.addAll(DuneFileType.getDefaultFilenames());
        return ImmutableList.copyOf(files);
    };

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
