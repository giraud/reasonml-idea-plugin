package com.reason.bs;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonBooleanLiteral;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import gnu.trove.THashSet;

import static com.reason.StringUtil.toFirstUpper;

public class BsConfig {

    @Nullable
    private final Path m_basePath;
    @NotNull
    private final String m_name;
    @NotNull
    private final String m_namespace;
    @NotNull
    private final List<Path> m_deps;
    private final String m_rootBsPlatform;
    private Set<String> m_sources;

    private BsConfig(@Nullable VirtualFile rootFile, @NotNull String name, boolean hasNamespace, @Nullable List<Path> deps) {
        m_basePath = rootFile == null ? null : FileSystems.getDefault().getPath(rootFile.getPath());
        m_name = name;
        m_namespace = hasNamespace ? toNamespace(name) : "";
        m_rootBsPlatform = FileSystems.getDefault().getPath("node_modules", "bs-platform").toString();
        m_deps = deps == null ? new ArrayList<>() : deps;
    }

    @NotNull
    public String getNamespace() {
        return m_namespace;
    }

    public boolean hasNamespace() {
        return !m_namespace.isEmpty();
    }

    boolean accept(@Nullable String canonicalPath) {
        if (canonicalPath == null || m_basePath == null) {
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
    public static BsConfig read(@NotNull Project project, @NotNull VirtualFile bsConfigFromFile) {
        PsiFile file = PsiManager.getInstance(project).findFile(bsConfigFromFile);
        assert file != null;
        return BsConfig.read(bsConfigFromFile.getParent(), file, false);
    }

    @NotNull
    public static BsConfig read(@Nullable VirtualFile rootFile, @NotNull PsiFile jsonWithComments, boolean useExternal) {
        Set<String> sources = new THashSet<>();

        JsonFile json = (JsonFile) jsonWithComments;
        JsonValue topLevelValue = json.getTopLevelValue();
        if (topLevelValue instanceof JsonObject) {
            JsonObject top = (JsonObject) topLevelValue;

            String name = "";
            JsonProperty nameProp = top.findProperty("name");
            if (nameProp != null) {
                JsonValue value = nameProp.getValue();
                if (value instanceof JsonStringLiteral) {
                    name = ((JsonStringLiteral) value).getValue();
                }
            }

            boolean hasNamespace = false;
            JsonProperty namespaceProp = top.findProperty("namespace");
            if (namespaceProp != null) {
                JsonValue value = namespaceProp.getValue();
                if (value instanceof JsonBooleanLiteral) {
                    hasNamespace = ((JsonBooleanLiteral) value).getValue();
                }
            }

            List<Path> paths = new ArrayList<>();

            if (useExternal) {
                // bs-platform/bsconfig.json
                JsonProperty extProp = top.findProperty("bs-external-includes");
                JsonValue extIncludes = extProp == null ? null : extProp.getValue();
                if (extIncludes instanceof JsonArray) {
                    for (JsonValue item : ((JsonArray) extIncludes).getValueList()) {
                        if (item instanceof JsonStringLiteral) {
                            sources.add(((JsonStringLiteral) item).getValue());
                        }
                    }
                }
                sources.add("lib/ocaml"); // Because of Belt !
            } else {
                JsonProperty srcProp = top.findProperty("sources");
                if (srcProp != null) {
                    JsonValue value = srcProp.getValue();
                    if (value instanceof JsonStringLiteral) {
                        sources.add(((JsonStringLiteral) value).getValue());
                    } else if (value instanceof JsonObject) {
                        String src = parseSourceItem((JsonObject) value);
                        if (src != null) {
                            sources.add(src);
                        }
                    } else if (value instanceof JsonArray) {
                        for (JsonValue item : ((JsonArray) value).getValueList()) {
                            if (item instanceof JsonStringLiteral) {
                                sources.add(((JsonStringLiteral) item).getValue());
                            } else if (item instanceof JsonObject) {
                                String src = parseSourceItem((JsonObject) item);
                                if (src != null) {
                                    sources.add(src);
                                }
                            }
                        }
                    }
                }

                JsonProperty depsProp = top.findProperty("bs-dependencies");
                if (depsProp != null) {
                    JsonValue value = depsProp.getValue();
                    if (value instanceof JsonArray) {
                        JsonArray values = (JsonArray) value;
                        for (JsonValue item : values.getValueList()) {
                            if (item instanceof JsonStringLiteral) {
                                String itemValue = ((JsonStringLiteral) item).getValue();
                                if (!itemValue.isEmpty()) {
                                    paths.add(FileSystems.getDefault().getPath("node_modules", itemValue, "lib"));
                                }
                            }
                        }
                    }
                }
            }

            BsConfig bsConfig = new BsConfig(rootFile, name, hasNamespace, paths);
            bsConfig.m_sources = sources;

            return bsConfig;
        }

        throw new RuntimeException("Not a Bucklescript config");
    }

    @Nullable
    private static String parseSourceItem(@NotNull JsonObject obj) {
        JsonProperty srcProp = obj.findProperty("dir");
        if (srcProp != null) {
            JsonValue value = srcProp.getValue();
            if (value instanceof JsonStringLiteral) {
                return ((JsonStringLiteral) value).getValue();
            }
        }
        return null;
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

    @NotNull
    public String getName() {
        return m_name;
    }

    public boolean isInSources(@NotNull VirtualFile file) {
        if (m_basePath != null) {
            Path relativePath = m_basePath.relativize(new File(file.getPath()).toPath());
            for (String source : m_sources) {
                if (relativePath.startsWith(source)) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    List<Path> getDependencies() {
        return m_deps;
    }
}
