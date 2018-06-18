package com.reason.jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.Collections;
import java.util.List;

public class Service extends BuilderService {
    @NotNull
    @Override
    public List<? extends BuildTargetType<?>> getTargetTypes() {
        return Collections.singletonList(new FileBuildTargetType());
    }

    @NotNull
    @Override
    public List<? extends TargetBuilder<?, ?>> createBuilders() {
        return Collections.singletonList(new FileTargetBuilder(new FileBuildTargetType()));
    }

}
