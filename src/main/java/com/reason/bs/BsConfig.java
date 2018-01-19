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

    private final String[] m_deps;

    private BsConfig(@Nullable String[] deps) {
        if (deps == null) {
            m_deps = new String[0];
        } else {
            m_deps = new String[deps.length + 1];
            System.arraycopy(deps, 0, m_deps, 0, deps.length);
            m_deps[deps.length] = "bs-platform/lib/ocaml/js.ml"; // all files but the ones with _ ?
        }
    }

    @NotNull
    static BsConfig read(@NotNull VirtualFile bsconfig) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bsconfig.getInputStream()))) {
                // Quick and dirty code to read json values from bsconfig
                StringBuilder content = new StringBuilder();
                reader.lines().forEach(line -> content.append(line.trim()));
                // extract bs dependencies
                Matcher matcher = DEPS_REGEXP.matcher(content.toString());

                String[] deps = null;
                if (matcher.matches()) {
                    String[] tokens = matcher.group(1).split(",");
                    deps = new String[tokens.length];
                    for (int i = 0; i < tokens.length; i++) {
                        String token = tokens[i].trim();
                        deps[i] = token.substring(1, token.length() - 1) + "/lib";
                    }
                }

                return new BsConfig(deps);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean accept(String canonicalPath) {
        if (canonicalPath.contains("node_modules") && m_deps != null) {
            for (String dep : m_deps) {
                if (canonicalPath.contains(dep)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }
}
