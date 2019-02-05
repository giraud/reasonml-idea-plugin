package com.reason.build.bs;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Joiner;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.reason.StringUtil.toFirstUpper;

public class BsConfig {

    private static final Pattern DEPS_REGEXP = Pattern.compile(".*\"bs-dependencies\":\\s*\\[(.*?)].*");
    private static final Pattern NAMESPACE_REGEXP = Pattern.compile(".*\"namespace\":\\s*(true|false).*");

    private final static Logger LOG = Logger.getInstance("ReasonML.bsConfig");

    @NotNull
    private final Path m_basePath;
    @NotNull private final String m_name;
    @NotNull
    private final String m_namespace;
    @NotNull
    private final Path[] m_deps;
    private final String m_rootBsPlatform;
    private Set<String> m_sources;

    private BsConfig(@Nullable VirtualFile rootFile, @NotNull String name, boolean hasNamespace, @Nullable Path[] deps) {
        m_basePath = rootFile == null ? null : FileSystems.getDefault().getPath(rootFile.getPath());
        m_name = name;
        m_namespace = hasNamespace ? toNamespace(name) : "";
        m_rootBsPlatform = FileSystems.getDefault().getPath("node_modules", "bs-platform").toString();
        m_deps = deps == null ? new Path[]{} : deps;
    }

    @NotNull
    public String getNamespace() {
        return m_namespace;
    }

    public boolean hasNamespace() {
        return !m_namespace.isEmpty();
    }

    boolean accept(@Nullable String canonicalPath) {
        if (canonicalPath == null) {
            return false;
        }

        Path relativePath = m_basePath.relativize(new File(canonicalPath).toPath());
        if (relativePath.startsWith("node_modules")) {
            if (relativePath.startsWith(m_rootBsPlatform)) {
                return true;
            }
            for (Path dep : m_deps) {
                if (relativePath.startsWith(dep)) {
                    return true;
                }
            }
            return false;
        }

        return !relativePath.startsWith("..");
    }

    @NotNull
    public static BsConfig read(@NotNull VirtualFile file) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                // Quick and dirty code to read json values from bsconfig
                StringBuilder content = new StringBuilder();
                reader.lines().forEach(content::append);
                return read(file.getParent(), file.getPath(), content.toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @NotNull
    public static BsConfig read(@Nullable VirtualFile rootFile, @NotNull String path, @NotNull String jsonWithComments) {
        Set<String> sources = new THashSet<>();

        String jsonContent = normaliseJson(jsonWithComments);
        //System.out.println("NORMALISED");
        //System.out.println(jsonContent);
        //System.out.println("NORMALISED");
        try {
            JsonValue parse = Json.parse(jsonContent);
            JsonObject object = parse.asObject();

            String name = object.getString("name", "");

            // read sources
            JsonValue jsonSources = object.get("sources");
            if (jsonSources.isString()) {
                sources.add(jsonSources.asString());
            } else if (jsonSources.isObject()) {
                String src = parseSourceItem(jsonSources);
                if (src != null) {
                    sources.add(src);
                }
            } else if (jsonSources.isArray()) {
                JsonArray jsonValues = jsonSources.asArray();
                for (JsonValue jsonValue : jsonValues) {
                    if (jsonValue.isString()) {
                        sources.add(jsonValue.asString());
                    } else if (jsonValue.isObject()) {
                        String src = parseSourceItem(jsonValue);
                        if (src != null) {
                            sources.add(src);
                        }
                    }
                }
            }

            boolean hasNamespace = readNamespace(jsonContent);
            Path[] deps = readDependencies(jsonContent);

            BsConfig bsConfig = new BsConfig(rootFile, name, hasNamespace, deps);
            bsConfig.m_sources = sources;

            return bsConfig;
        } catch (Exception ex) {
            System.out.println("EXCEPTION " + ex.getMessage());
            System.out.println("json from " + path);
            System.out.println(jsonContent);
            throw ex;
        }
    }

    @NotNull
    private static String normaliseJson(@NotNull String jsonWithComments) {
        return jsonWithComments.replaceAll("//.*\n", "").replaceAll("\n", " ").replaceAll("/\\*.*?\\*/", "");
    }

    @Nullable
    private static String parseSourceItem(@NotNull JsonValue jsonSources) {
        JsonObject sourceItem = jsonSources.asObject();
        return sourceItem.getString("dir", null);
    }

    @Nullable
    static Path[] readDependencies(@NotNull String content) {
        List<Path> result = null;

        Matcher matcher = DEPS_REGEXP.matcher(content);
        if (matcher.matches()) {
            String[] tokens = matcher.group(1).split(",");
            result = new ArrayList<>();
            for (String token1 : tokens) {
                String token = token1.trim();
                if (2 < token.length()) {
                    result.add(FileSystems.getDefault().getPath("node_modules", token.substring(1, token.length() - 1), "lib"));
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Dependencies found: [" + Joiner.join(", ", result) + "]");
        }

        return result == null ? null : result.toArray(new Path[0]);
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
        result = new StringBuilder(toFirstUpper(tokens[0]));
        if (1 < tokens.length) {
            for (int i = 1; i < tokens.length; i++) {
                result.append(toFirstUpper(tokens[i]));
            }
        }

        return result.toString();
    }

    public Set<String> getSources() {
        return m_sources;
    }

    public String getName() {
        return m_name;
    }

    public boolean isInSources(@NotNull VirtualFile file) {
        Path relativePath = m_basePath.relativize(new File(file.getPath()).toPath());
        for (String source : m_sources) {
            if (relativePath.startsWith(source)) {
                return true;
            }
        }
        return false;
    }
}
