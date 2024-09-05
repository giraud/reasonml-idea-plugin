package com.reason.comp.bs;

import org.jetbrains.annotations.*;

import java.util.*;

public class BsConfig {
    private final String myName;
    private final String myNamespace;
    private final String myJsxVersion;
    private final String myJsxMode;
    private final boolean myUncurried;
    private final Set<String> myExternals = new HashSet<>();
    private final Set<String> mySources = new HashSet<>();
    private final Set<String> myDevSources = new HashSet<>();
    private final Set<String> myDeps = new HashSet<>();
    private final Set<String> myBscFlags = new HashSet<>();
    private final Set<String> myOpenedDeps = new HashSet<>();
    private final String[] myPpx;
    private boolean myUseExternalAsSource = false;

    BsConfig(@NotNull String name, @Nullable String namespace, @Nullable Set<String> sources,
             @Nullable Set<String> devSources, @Nullable Set<String> externals, @Nullable Set<String> deps,
             @NotNull Set<String> bscFlags, @Nullable List<String> ppx,
             @Nullable String jsxVersion, @Nullable String jsxMode, boolean uncurried) {
        myName = name;
        myNamespace = namespace == null ? "" : namespace;
        myJsxVersion = jsxVersion;
        myJsxMode = jsxMode;
        myUncurried = uncurried;
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
        }

        myBscFlags.addAll(bscFlags);
        for (String flag : myBscFlags) {
            if (flag.startsWith("-open ")) {
                String dependency = flag.substring(6).trim();
                if (!dependency.isEmpty()) {
                    myOpenedDeps.add(dependency);
                }
            }
        }
    }

    public @NotNull String getNamespace() {
        return myNamespace;
    }

    public boolean hasNamespace() {
        return !myNamespace.isEmpty();
    }

    public @NotNull Set<String> getSources() {
        return myUseExternalAsSource ? myExternals : mySources;
    }

    public @NotNull Set<String> getDevSources() {
        return myDevSources;
    }

    public @NotNull String getName() {
        return myName;
    }

    public @NotNull Set<String> getDependencies() {
        return myDeps;
    }

    public @NotNull Set<String> getBscFlags() {
        return myBscFlags;
    }

    public @NotNull Set<String> getOpenedDeps() {
        return myOpenedDeps;
    }

    public @Nullable String getJsxVersion() {
        return myJsxVersion;
    }

    public @Nullable String getJsxMode() {
        return myJsxMode;
    }

    public boolean isUncurried() {
        return myUncurried;
    }

    public @NotNull String[] getPpx() {
        return myPpx;
    }

    public void setUseExternalAsSource(boolean useExternalAsSource) {
        myUseExternalAsSource = useExternalAsSource;
    }
}
