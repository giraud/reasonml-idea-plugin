package com.reason.jps.builder;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildRootIndex;
import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.builders.BuildTargetRegistry;
import org.jetbrains.jps.builders.TargetOutputIndex;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.jps.util.JpsPathUtil.urlToFile;

public class FileBuildTarget extends BuildTarget<FileRootDescriptor> {
    private static final Logger LOG = Logger.getInstance("ReasonML.fileBuildTarget");

    private final FileRootDescriptor m_rootDescriptor;
    private final File m_outputLocation;

    public FileBuildTarget(FileBuildTargetType type, JpsModule module, File file) {
        super(type);

        m_rootDescriptor = new FileRootDescriptor(this, module, file);
        File sourceLocation = urlToFile(module.getContentRootsList().getUrls().get(0));

        File destination = new File(sourceLocation.getParentFile(), "build");
        m_outputLocation = new File(destination, trimExtension(file.getName()) + ".ml"); //FIXME Should be whole path
//        assert rootDescriptor.getRootId().equals(file.getPath());
        LOG.info("fileBuildTarget:: " + m_rootDescriptor + " " + m_outputLocation);
    }

    @NotNull
    private static String trimExtension(@NotNull String name) {
        int index = name.lastIndexOf('.');
        return (index < 0) ? name : name.substring(0, index);
    }

    @Override
    public String getId() {
        return m_rootDescriptor.getRootId();
    }

    @Override
    public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry targetRegistry, TargetOutputIndex outputIndex) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<FileRootDescriptor> computeRootDescriptors(JpsModel model, ModuleExcludeIndex index, IgnoredFileIndex ignoredFileIndex, BuildDataPaths dataPaths) {
        return Collections.singletonList(m_rootDescriptor);
    }

    @Nullable
    @Override
    public FileRootDescriptor findRootDescriptor(String rootId, BuildRootIndex rootIndex) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "ReasonML-build-file " + m_rootDescriptor.getRootFile().getName();
    }

    @NotNull
    @Override
    public Collection<File> getOutputRoots(CompileContext context) {
        return Collections.singletonList(m_outputLocation);
    }
}
