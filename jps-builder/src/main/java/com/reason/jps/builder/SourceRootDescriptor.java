package com.reason.jps.builder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtilRt;
import com.reason.jps.Builder;
import com.reason.jps.Target;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import java.io.File;
import java.io.FileFilter;

public class SourceRootDescriptor extends BuildRootDescriptor {
    private static final Logger LOG = Logger.getInstance(SourceRootDescriptor.class);

    private File myRoot;
    private final Target myTarget;

    public SourceRootDescriptor(File root, Target target) {
        myRoot = root;
        myTarget = target;
    }

    @Override
    public String getRootId() {
        return myRoot.getAbsolutePath();
    }

    @Override
    public File getRootFile() {
        return myRoot;
    }

    @Override
    public BuildTarget<?> getTarget() {
        return myTarget;
    }

    @NotNull
    @Override
    public FileFilter createFileFilter() {
        return file -> {
            String name = file.getName();
            LOG.info("filter " + name + " " + (FileUtilRt.extensionEquals(name, Builder.REASON_SOURCE_EXTENSION) ||
                    FileUtilRt.extensionEquals(name, Builder.OCAML_SOUCE_EXTENSION)));
            return FileUtilRt.extensionEquals(name, Builder.REASON_SOURCE_EXTENSION) ||
                    FileUtilRt.extensionEquals(name, Builder.OCAML_SOUCE_EXTENSION);
        };
    }
}
