package com.reason.build.bs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Joiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BsConfig {

    private static final Pattern DEPS_REGEXP = Pattern.compile(".*\"bs-dependencies\":\\s*\\[(.*?)].*");
    private static final Pattern NAME_REGEXP = Pattern.compile(".*\"name\":\\s*\"([^\"]*?)\".*");
    private static final Pattern NAMESPACE_REGEXP = Pattern.compile(".*\"namespace\":\\s*(true|false).*");

    private final static Logger m_log = Logger.getInstance("ReasonML.bsConfig");

    private final Path m_basePath;
    private final String m_namespace;
    private final Path[] m_deps;
    private final Path m_pervasives;

    private BsConfig(VirtualFile rootFile, @NotNull String name, boolean hasNamespace, @NotNull Path[] bsPlatformDeps, @Nullable Path[] deps) {
        m_basePath = FileSystems.getDefault().getPath(rootFile.getPath(), "node_modules");
        m_namespace = hasNamespace ? toNamespace(name) : "";
        m_pervasives = locateFile(rootFile, "pervasives.mli");

        if (deps == null) {
            m_deps = bsPlatformDeps;
        } else {
            m_deps = new Path[deps.length + bsPlatformDeps.length];
            System.arraycopy(deps, 0, m_deps, 0, deps.length);
            System.arraycopy(bsPlatformDeps, 0, m_deps, deps.length, bsPlatformDeps.length);
        }
    }

    @NotNull
    String getNamespace() {
        return m_namespace;
    }

    boolean accept(@Nullable String canonicalPath) {
        if (canonicalPath == null) {
            return false;
        }

        Path relativePath = m_basePath.relativize(new File(canonicalPath).toPath());
        if (relativePath.startsWith("node_modules") && m_deps != null) {
            for (Path dep : m_deps) {
                if (relativePath.startsWith(dep) || relativePath.startsWith(m_pervasives)) {
                    return true;
                }
            }
            return false;
        }

        return !relativePath.startsWith("..");
    }

    @NotNull
    static BsConfig read(@NotNull VirtualFile bsconfig) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bsconfig.getInputStream()))) {
                // Quick and dirty code to read json values from bsconfig
                StringBuilder content = new StringBuilder();
                reader.lines().forEach(line -> content.append(line.trim()));

                String jsonContent = content.toString();
                String name = readName(jsonContent);
                boolean hasNamespace = readNamespace(jsonContent);
                Path[] deps = readDependencies(jsonContent);

                // find location of bs dependencies, could be lib/ocaml or jscomp/runtime (depends on os)
                VirtualFile rootFile = bsconfig.getParent();
                Path[] bsDeps = new Path[]{
                        locateFile(rootFile, "js.mli"),
                        locateFile(rootFile, "js.ml"),
                        locateFile(rootFile, "belt.mli"),
                        locateFile(rootFile, "belt.ml")
                };

                return new BsConfig(rootFile, name, hasNamespace, bsDeps, deps);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static Path locateFile(@NotNull VirtualFile rootFile, @NotNull String filename) {
        VirtualFile jsPath = rootFile.findFileByRelativePath("node_modules/bs-platform/lib/ocaml/" + filename);
        if (jsPath == null) {
            jsPath = rootFile.findFileByRelativePath("node_modules/bs-platform/jscomp/runtime/" + filename);
            if (jsPath != null) {
                return FileSystems.getDefault().getPath("bs-platform", "jscomp", "runtime", filename);
            }
        }
        return FileSystems.getDefault().getPath("bs-platform", "lib", "ocaml", filename);
    }

    @Nullable
    private static Path[] readDependencies(@NotNull String content) {
        Path[] result = null;

        Matcher matcher = DEPS_REGEXP.matcher(content);
        if (matcher.matches()) {
            String[] tokens = matcher.group(1).split(",");
            result = new Path[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                result[i] = FileSystems.getDefault().getPath(token.substring(1, token.length() - 1), "lib");
            }
        }

        if (m_log.isDebugEnabled()) {
            m_log.debug("Dependencies found: [" + Joiner.join(", ", result) + "]");
        }

        return result;
    }

    @NotNull
    private static String readName(@NotNull String content) {
        String result = "";

        Matcher matcher = NAME_REGEXP.matcher(content);
        if (matcher.matches()) {
            result = matcher.group(1);
        }

        return result;
    }

    private static boolean readNamespace(@NotNull String content) {
        boolean result = false;

        Matcher matcher = NAMESPACE_REGEXP.matcher(content);
        if (matcher.matches()) {
            result = Boolean.valueOf(matcher.group(1));
        }

        return result;
    }

    @NotNull
    private static String toNamespace(@NotNull String name) {
        StringBuilder result = new StringBuilder(name.replaceAll("_", ""));

        String[] tokens = result.toString().split("[-@/]");
        if (1 < tokens.length) {
            result = new StringBuilder(upperCaseFirst(tokens[0]));
            for (int i = 1; i < tokens.length; i++) {
                result.append(upperCaseFirst(tokens[i]));
            }
        }

        return result.toString();
    }

    @NotNull
    private static String upperCaseFirst(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String result = value.substring(0, 1).toUpperCase(Locale.getDefault());
        result += value.substring(1);
        return result;
    }
}
