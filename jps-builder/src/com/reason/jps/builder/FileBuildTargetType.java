package com.reason.jps.builder;

import com.intellij.openapi.diagnostic.Logger;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intellij.openapi.util.io.FileUtil.findFilesByMask;

public class FileBuildTargetType extends BuildTargetType<FileBuildTarget> {
    private static final Logger LOG = Logger.getInstance("ReasonML.fileBuildTargetType");
    private static final Pattern SOURCE_FILES = Pattern.compile(".*\\.ml");

    public FileBuildTargetType() {
        super("REASONML_FILE_TARGET"/*, true?*/);
    }

    @NotNull
    @Override
    public List<FileBuildTarget> computeAllTargets(@NotNull JpsModel model) {
        LOG.info("computeAllTargets");
        return model.getProject().getModules().stream()
                .flatMap(this::moduleBuildTargets).collect(Collectors.toList());
    }

    @NotNull
    private Stream<FileBuildTarget> moduleBuildTargets(JpsModule module) {
        List<String> urls = module.getContentRootsList().getUrls();

        LOG.info("URLs: " + urls);
        Stream<File> paths = urls.stream().map(JpsPathUtil::urlToFile);

        Stream<File> files = paths.flatMap(path -> findFilesByMask(SOURCE_FILES, path).stream());

        return files.map(file -> new FileBuildTarget(this, module, file));
    }

    @NotNull
    @Override
    public BuildTargetLoader<FileBuildTarget> createLoader(@NotNull JpsModel model) {
        LOG.info("createLoader");
        return createLoader(this, model);
    }


    public static <Target extends BuildTarget<?>> BuildTargetLoader<Target> createLoader(@NotNull final BuildTargetType<Target> type,
                                                                                         @NotNull final JpsModel model) {
        final Map<String, Target> targetMap = new THashMap<>();

        for (Target target: type.computeAllTargets(model)) {
            targetMap.put(target.getId(), target);
        }
        return new BuildTargetLoader<Target>() {
            @Nullable
            @Override
            public Target createTarget(@NotNull String targetId) {
                LOG.info("Creating loader for: " + targetId);
                Target target = targetMap.get(targetId);
                if (target == null) {
                    LOG.info("Target id: " + targetId + " " + targetMap.keySet().stream().findFirst());
                }
                return target;
            }
        };
    }

}
