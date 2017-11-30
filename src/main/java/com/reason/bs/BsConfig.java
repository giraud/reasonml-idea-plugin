package com.reason.bs;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BsConfig {
    private static Pattern DEPS_REGEXP = Pattern.compile(".*\"bs-dependencies\":\\s*\\[(.*?)].*");

    private final String[] m_deps;

    BsConfig(String[] deps) {
        m_deps = deps;
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
        if (canonicalPath.contains("node_modules")) {
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
