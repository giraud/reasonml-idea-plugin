package com.reason.bs;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BsConfig {

    private static Pattern DEPS_REGEXP = Pattern.compile(".*\"bs-dependencies\":\\s*\\[(.*?)].*");
    private static Pattern NAME_REGEXP = Pattern.compile(".*\"name\":\\s*\"([^\"]*?)\".*");

    private static String[] PERVASIVES = new String[]{
            // all files but the ones with _ ?
            "bs-platform/lib/ocaml/js.mli",
    };

    private final String m_name;
    private final String[] m_deps;

    private BsConfig(String name, @Nullable String[] deps) {
        m_name = name;

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
    static BsConfig read(@NotNull VirtualFile bsconfig) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bsconfig.getInputStream()))) {
                // Quick and dirty code to read json values from bsconfig
                StringBuilder content = new StringBuilder();
                reader.lines().forEach(line -> content.append(line.trim()));

                String name = readName(content);
                String[] deps = readDependencies(content);

                return new BsConfig(name, deps);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] readDependencies(StringBuilder content) {
        String[] result = null;

        Matcher matcher = DEPS_REGEXP.matcher(content.toString());
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

    private static String readName(StringBuilder content) {
        String result = null;

        Matcher matcher = NAME_REGEXP.matcher(content.toString());
        if (matcher.matches()) {
            result = matcher.group(1);
        }

        return result;
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
}
