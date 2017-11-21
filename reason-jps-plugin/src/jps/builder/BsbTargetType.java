package jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.model.JpsModel;

import java.util.ArrayList;
import java.util.List;

public class BsbTargetType extends BuildTargetType<BsbTarget> {
    public static final BsbTargetType PRODUCTION = new BsbTargetType("bsb-production", false);

    protected BsbTargetType(String bsb, boolean tests) {
        super(bsb);
    }

    @NotNull
    @Override
    public List<BsbTarget> computeAllTargets(@NotNull JpsModel model) {
        List<BsbTarget> targets = new ArrayList<>();
        return targets;
    }

    @NotNull
    @Override
    public BuildTargetLoader createLoader(@NotNull JpsModel model) {
        return new BuildTargetLoader() {
            @Nullable
            @Override
            public BuildTarget<?> createTarget(@NotNull String targetId) {
                return null;
            }
        };
    }
}
