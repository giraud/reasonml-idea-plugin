package com.reason.jps.builder;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileRootDescriptor extends BuildRootDescriptor {
    private static final Logger LOG = Logger.getInstance("ReasonML.fileRootDescriptor");

    private final File m_path;
    private final FileBuildTarget m_target;
    private final List<File> m_contentRoots;

    FileRootDescriptor(FileBuildTarget target, @NotNull JpsModule module, File file) {
        m_path = file;
        m_target = target;
        m_contentRoots = module.getContentRootsList().getUrls().stream().map(JpsPathUtil::urlToFile).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public String getRootId() {
        return m_path.getAbsolutePath();
    }

    @Override
    public File getRootFile() {
        return m_path;
    }

    @Override
    public FileBuildTarget getTarget() {
        return m_target;
    }
}
