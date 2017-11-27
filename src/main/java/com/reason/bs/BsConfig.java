package com.reason.bs;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BsConfig /*Project aware ?*/ {
    private static Pattern DEPS_REGEXP = Pattern.compile(".*\"bs-dependencies\":\\s*\\[(.*?)].*");

    private static BsConfig INSTANCE;
    private String[] m_deps;

    public static BsConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BsConfig();
        }
        return INSTANCE;
    }

    public static void read(VirtualFile bsconfig) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bsconfig.getInputStream()))) {
                // Quick and dirty code to read json values from bsconfig
                StringBuilder content = new StringBuilder();
                reader.lines().forEach(line -> content.append(line.trim()));
                // extract bs dependencies
                Matcher matcher = DEPS_REGEXP.matcher(content.toString());
                if (matcher.matches()) {
                    String[] tokens = matcher.group(1).split(",");
                    String[] deps = new String[tokens.length];
                    for (int i = 0; i < tokens.length; i++) {
                        String token = tokens[i].trim();
                        deps[i] = token.substring(1, token.length() - 1) + "/lib";
                    }
                    getInstance().updateDeps(deps);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateDeps(String[] deps) {
        m_deps = deps;
    }

    public boolean accept(String canonicalPath) {
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
