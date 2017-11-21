package jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.*;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class BsbTarget extends BuildTarget<BsbSourceRootDescriptor> {
    protected BsbTarget(BuildTargetType<?> targetType) {
        super(targetType);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry targetRegistry, TargetOutputIndex outputIndex) {
        return null;
    }

    @NotNull
    @Override
    public List<BsbSourceRootDescriptor> computeRootDescriptors(JpsModel model, ModuleExcludeIndex index, IgnoredFileIndex ignoredFileIndex, BuildDataPaths dataPaths) {
        return null;
    }

    @Nullable
    @Override
    public BsbSourceRootDescriptor findRootDescriptor(String rootId, BuildRootIndex rootIndex) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return null;
    }

    @NotNull
    @Override
    public Collection<File> getOutputRoots(CompileContext context) {
        return null;
    }
}
