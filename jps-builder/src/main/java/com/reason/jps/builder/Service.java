package com.reason.jps.builder;
import com.intellij.openapi.diagnostic.Logger;
import com.reason.jps.Builder;
import com.reason.jps.target.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Service extends BuilderService {
    @NotNull
    @Override
    public List<? extends BuildTargetType<?>> getTargetTypes() {
        return Arrays.asList(Type.PRODUCTION, Type.TEST);
    }

    @NotNull
    @Override
    public List<? extends TargetBuilder<?, ?>> createBuilders() {
        return Collections.singletonList(new Builder());
    }

}
