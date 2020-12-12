package com.reason.module;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * Builder to create a new OCaml module.
 */
public class OCamlModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {
    private @Nullable List<Pair<String, String>> m_sourcePaths;

    @Override
    public @NotNull ModuleType<OCamlModuleBuilder> getModuleType() {
        return OCamlModuleType.getInstance();
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        rootModel.inheritSdk();

        ContentEntry contentEntry = doAddContentEntry(rootModel);
        if (contentEntry != null) {
            List<Pair<String, String>> sourcePaths = getSourcePaths();

            if (sourcePaths != null) {
                for (final Pair<String, String> sourcePath : sourcePaths) {
                    String first = sourcePath.first;
                    boolean created = new File(first).mkdirs();
                    if (created) {
                        VirtualFile sourceRoot =
                                LocalFileSystem.getInstance()
                                        .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first));
                        if (sourceRoot != null) {
                            contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second);
                        }
                    }
                }
            }
        }
    }

    @Override
    public @Nullable ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        // return new OCamlModuleWizardStep(context);
        return null;
    }

    @Override
    public @Nullable List<Pair<String, String>> getSourcePaths() {
        if (m_sourcePaths == null) {
            List<Pair<String, String>> paths = new ArrayList<>();
            @NonNls String path = getContentEntryPath() + File.separator + "src";
            boolean created = new File(path).mkdirs();
            if (created) {
                paths.add(Pair.create(path, ""));
                return paths;
            }
        }
        return m_sourcePaths;
    }

    @Override
    public void setSourcePaths(@Nullable List<Pair<String, String>> sourcePaths) {
        m_sourcePaths = sourcePaths != null ? new ArrayList<>(sourcePaths) : null;
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        if (m_sourcePaths == null) {
            m_sourcePaths = new ArrayList<>();
        }
        m_sourcePaths.add(sourcePathInfo);
    }
}
