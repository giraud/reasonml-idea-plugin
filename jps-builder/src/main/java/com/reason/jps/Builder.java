package com.reason.jps;

import com.intellij.openapi.diagnostic.Logger;
import com.reason.jps.builder.SourceRootDescriptor;
import com.reason.jps.target.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.io.IOException;
import java.util.Arrays;

public class Builder extends TargetBuilder<SourceRootDescriptor, Target> {
    public static final String BUILDER_NAME = "Reason Builder";
    //
    public static final String REASON_SOURCE_EXTENSION = "re";
    public static final String OCAML_SOUCE_EXTENSION = "ml";

    private final static Logger LOG = Logger.getInstance(Builder.class);

    public Builder() {
        super(Arrays.asList(Type.PRODUCTION, Type.TEST));
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return BUILDER_NAME;
    }

    @Override
    public void build(@NotNull Target target, @NotNull DirtyFilesHolder<SourceRootDescriptor, Target> holder, @NotNull BuildOutputConsumer outputConsumer, @NotNull CompileContext context) throws ProjectBuildException, IOException {

    }
}
