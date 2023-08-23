package com.reason.comp.bs;

import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class BsConfig {
    private final String myName;
    private final String myNamespace;
    private final String myJsxVersion;
    private final String myRootBsPlatform;
    private final Set<String> myExternals = new HashSet<>();
    private final Set<String> mySources = new HashSet<>();
    private final Set<String> myDevSources = new HashSet<>();
    private final Set<String> myDeps = new HashSet<>();
    private final Set<String> myBscFlags = new HashSet<>();
    private final Set<String> myOpenedDeps = new HashSet<>();
    private final Set<Path> myPaths = new HashSet<>();
    private final String[] myPpx;
    private boolean myUseExternalAsSource = false;
    private Path myBasePath = null;

    BsConfig(@NotNull String name, @Nullable String namespace, @Nullable String jsxVersion, @Nullable Set<String> sources,
             @Nullable Set<String> devSources, @Nullable Set<String> externals, @Nullable Set<String> deps, @NotNull Set<String> bscFlags, @Nullable List<String> ppx) {
        myName = name;
        myNamespace = namespace == null ? "" : namespace;
        myJsxVersion = jsxVersion;
        myRootBsPlatform = FileSystems.getDefault().getPath("node_modules", "bs-platform").toString();
        myPpx = ppx == null ? new String[0] : ppx.toArray(new String[0]);
        if (sources != null) {
            mySources.addAll(sources);
        }
        if (devSources != null) {
            myDevSources.addAll(devSources);
        }
        if (externals != null) {
            myExternals.addAll(externals);
        }
        if (deps != null) {
            myDeps.addAll(deps);
            myPaths.addAll(
                    deps.stream()
                            .map(dep -> FileSystems.getDefault().getPath("node_modules", dep, "lib"))
                            .collect(Collectors.toSet()));
        }

        myBscFlags.addAll(bscFlags);
        for (String flag : myBscFlags) {
            if (flag.startsWith("-open ")) {
                myOpenedDeps.add(flag.substring(6).trim());
            }
        }
    }

    public void setRootFile(@Nullable VirtualFile rootFile) {
        myBasePath = rootFile == null ? null : FileSystems.getDefault().getPath(rootFile.getPath());
    }

    @NotNull
    public String getNamespace() {
        return myNamespace;
    }

    public boolean hasNamespace() {
        return !myNamespace.isEmpty();
    }

    boolean accept(@Nullable String canonicalPath) {
        if (canonicalPath == null || myBasePath == null) {
            return false;
        }

        Path relativePath = myBasePath.relativize(new File(canonicalPath).toPath());
        if (relativePath.startsWith("node_modules")) {
            if (relativePath.startsWith(myRootBsPlatform)) {
                return true;
            }
            for (Path path : myPaths) {
                if (relativePath.startsWith(path)) {
                    return true;
                }
            }
            return false;
        }

        return !relativePath.startsWith("..");
    }

    @NotNull
    public Set<String> getSources() {
        return myUseExternalAsSource ? myExternals : mySources;
    }

    @NotNull
    public Set<String> getDevSources() {
        return myDevSources;
    }

    @NotNull
    public String getName() {
        return myName;
    }

    @NotNull
    public Set<String> getDependencies() {
        return myDeps;
    }

    @NotNull
    public Set<String> getBscFlags() {
        return myBscFlags;
    }

    @NotNull
    public Set<String> getOpenedDeps() {
        return myOpenedDeps;
    }

    @Nullable
    public String getJsxVersion() {
        return myJsxVersion;
    }

    public String @NotNull [] getPpx() {
        return myPpx;
    }

    public void setUseExternalAsSource(boolean useExternalAsSource) {
        myUseExternalAsSource = useExternalAsSource;
    }
}
