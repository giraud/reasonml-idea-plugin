package jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

public class BsbBuilderService extends BuilderService {
    @NotNull
    @Override
    public List<? extends BuildTargetType<?>> getTargetTypes() {
        return singletonList(BsbTargetType.PRODUCTION);
    }

    @NotNull
    @Override
    public List<? extends TargetBuilder<?, ?>> createBuilders() {
        return Collections.emptyList();
    }
}

