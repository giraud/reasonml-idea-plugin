package com.reason.bs;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BsConfig {

    private static Pattern DEPS_REGEXP = Pattern.compile(".*\"bs-dependencies\":\\s*\\[(.*?)].*");
    private static Pattern NAME_REGEXP = Pattern.compile(".*\"name\":\\s*\"([^\"]*?)\".*");
    private static Pattern NAMESPACE_REGEXP = Pattern.compile(".*\"namespace\":\\s*(true|false).*");

    private static String[] PERVASIVES = new String[]{
            // all files but the ones with _ ?
            "bs-platform/lib/ocaml/js.mli",
            "bs-platform/lib/ocaml/js.ml",
            "bs-platform/lib/ocaml/belt.mli",
            "bs-platform/lib/ocaml/belt.ml",
    };

    private final String m_namespace;
    private final String[] m_deps;

    private BsConfig(String name, boolean hasNamespace, @Nullable String[] deps) {
        m_namespace = hasNamespace ? toNamespace(name) : "";

        if (deps == null) {
            m_deps = new String[PERVASIVES.length];
            System.arraycopy(PERVASIVES, 0, m_deps, 0, PERVASIVES.length);
        } else {
            m_deps = new String[deps.length + PERVASIVES.length];
            System.arraycopy(deps, 0, m_deps, 0, deps.length);
            System.arraycopy(PERVASIVES, 0, m_deps, deps.length, PERVASIVES.length);
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

        if (canonicalPath.contains("node_modules") && m_deps != null) {
            for (String dep : m_deps) {
                if (canonicalPath.contains(dep) || canonicalPath.contains("bs-platform/lib/ocaml/pervasives.ml")) {
                    return true;
                }
            }
            return false;
        }

        return true;
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
                String[] deps = readDependencies(jsonContent);

                return new BsConfig(name, hasNamespace, deps);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static String[] readDependencies(@NotNull String content) {
        String[] result = null;

        Matcher matcher = DEPS_REGEXP.matcher(content);
        if (matcher.matches()) {
            String[] tokens = matcher.group(1).split(",");
            result = new String[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                result[i] = token.substring(1, token.length() - 1) + "/lib";
            }
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

    private static String toNamespace(@NotNull String name) {
        String result = name.replaceAll("_", "");

        String[] tokens = name.split("-");
        if (1 < tokens.length) {
            result = tokens[0];
            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i];
                result += token.substring(0, 1).toUpperCase(Locale.getDefault());
                result += token.substring(1);
            }
        }

        return result;
    }
}
