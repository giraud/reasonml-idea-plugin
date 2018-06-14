package com.reason.jps.target;

import com.reason.jps.Target;
import com.reason.model.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.ModuleBasedBuildTargetType;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsTypedModule;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.diagnostic.Logger;
public class Type extends ModuleBasedBuildTargetType<Target> {
    public static final Type PRODUCTION = new Type("reasonml-production", false);
    public static final Type TEST = new Type("reasonml-test", true);

    private static final Logger LOG = Logger.getInstance(Type.class);

    private final boolean m_tests;

    private Type(String elixir, boolean tests) {
        super(elixir);
        m_tests = tests;
    }

    @NotNull
    @Override
    public List<Target> computeAllTargets(@NotNull JpsModel model) {
        LOG.info("computeAllTargets " + model);
        List<Target> targets = new ArrayList<>();
        for (JpsTypedModule<JpsDummyElement> module: model.getProject().getModules(ModuleType.INSTANCE)) {
            targets.add(new Target(this, module));
        }
        return targets;
    }

    @NotNull
    @Override
    public BuildTargetLoader<Target> createLoader(@NotNull JpsModel model) {
        LOG.info("Type.createLoader");
        return new BuildTargetLoader<Target>() {
            @Nullable
            @Override
            public Target createTarget(@NotNull String targetId) {
                for (JpsTypedModule<JpsDummyElement> module: model.getProject().getModules(ModuleType.INSTANCE)) {
                    if (module.getName().equals(targetId)) {
                        return new Target(Type.this, module);
                    }
                }
                return null;
            }
        };
    }

    public boolean isTests() {
        return m_tests;
    }
}
