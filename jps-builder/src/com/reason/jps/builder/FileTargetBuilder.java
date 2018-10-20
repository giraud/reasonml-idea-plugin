package com.reason.jps.builder;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.io.IOException;
import java.util.Collections;

public class FileTargetBuilder extends TargetBuilder<FileRootDescriptor, FileBuildTarget> {
    private static final Logger LOG = Logger.getInstance("ReasonML.fileTargetBuilder");

    public FileTargetBuilder(FileBuildTargetType type) {
        super(Collections.singletonList(type));
    }

    @Override
    public void buildStarted(CompileContext context) {
        LOG.info("Build started");
    }


    @Override
    public void buildFinished(CompileContext context) {
        LOG.info("Build finished");
    }


    @Override
    public void build(@NotNull FileBuildTarget target, @NotNull DirtyFilesHolder<FileRootDescriptor, FileBuildTarget> holder, @NotNull BuildOutputConsumer outputConsumer, @NotNull CompileContext context) throws ProjectBuildException, IOException {
        LOG.info("Building " + target + " " + holder.hasDirtyFiles());

    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "ReasonML-file-target-builder";
    }
}
