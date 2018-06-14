package com.reason.jps;

import com.intellij.openapi.diagnostic.Logger;
import com.reason.jps.builder.SourceRootDescriptor;
import com.reason.jps.target.Type;
import com.reason.model.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.*;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.java.JpsJavaClasspathKind;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Target extends ModuleBasedTarget<SourceRootDescriptor> {
    private static final Logger LOG = Logger.getInstance(Target.class);

    public Target(Type targetType, @NotNull JpsModule module) {
        super(targetType, module);
    }

    @Override
    public String getId() {
        return myModule.getName();
    }

    @Override
    public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry targetRegistry, TargetOutputIndex outputIndex) {
        return computeDependencies();
    }

    public Collection<BuildTarget<?>> computeDependencies() {
        LOG.info("computeDependencies");
        List<BuildTarget<?>> dependencies = new ArrayList<>();

        Set<JpsModule> modules = JpsJavaExtensionService.dependencies(myModule).includedIn(JpsJavaClasspathKind.compile(isTests())).getModules();
        for (JpsModule module: modules) {
            if (module.getModuleType().equals(ModuleType.INSTANCE)) {
                dependencies.add(new Target(getReasonTargetType(), module));
            }
        }

        if (isTests()) {
            dependencies.add(new Target(Type.PRODUCTION, myModule));
        }

        return dependencies;
    }

    @NotNull
    @Override
    public List<SourceRootDescriptor> computeRootDescriptors(JpsModel model, ModuleExcludeIndex index, IgnoredFileIndex ignoredFileIndex, BuildDataPaths dataPaths) {
        return null;
    }

    @Nullable
    @Override
    public SourceRootDescriptor findRootDescriptor(String rootId, BuildRootIndex rootIndex) {
        return null;
    }

    @NotNull
    @Override
    public Collection<File> getOutputRoots(CompileContext context) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Reason '" + myModule.getName() + "' " + (isTests() ? "test" : "production");
    }

    @Override
    public boolean isTests() {
        return getReasonTargetType().isTests();
    }

    public Type getReasonTargetType() {
        return (Type) getTargetType();
    }
}
