package com.reason.bs;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.RmlNotification;
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

    private final String m_namespace;
    private final String[] m_deps;
    private final String m_pervasives;

    private BsConfig(@NotNull String name, boolean hasNamespace, @NotNull String bsDepsRelativePath, @Nullable String[] deps) {
        m_namespace = hasNamespace ? toNamespace(name) : "";
        m_pervasives = bsDepsRelativePath + "/pervasives.mli";

        // all files but the ones with _ ?
        String[] bsPlatformDeps = new String[]{
                bsDepsRelativePath + "/js.mli",
                bsDepsRelativePath + "/js.ml",
                bsDepsRelativePath + "/belt.mli",
                bsDepsRelativePath + "/belt.ml",
        };

        if (deps == null) {
            m_deps = bsPlatformDeps;
        } else {
            m_deps = new String[deps.length + bsPlatformDeps.length];
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

        if (canonicalPath.contains("node_modules") && m_deps != null) {
            for (String dep : m_deps) {
                if (canonicalPath.contains(dep) || canonicalPath.contains(m_pervasives)) {
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

                // find location of bs dependencies, could be lib/ocaml or jscomp/runtime (depends on os)
                String bsDepsRelativePath = "bs-platform/lib/ocaml";
                VirtualFile rootFile = bsconfig.getParent();
                VirtualFile jsPath = rootFile.findFileByRelativePath("node_modules/" + bsDepsRelativePath + "/js.mli");
                if (jsPath == null) {
                    jsPath = rootFile.findFileByRelativePath("node_modules/bs-platform/jscomp/runtime/js.mli");
                    if (jsPath == null) {
                        Notifications.Bus.notify(new RmlNotification("Bsb", "Can't find location of js.mli, completion is missing bs-platform modules", NotificationType.WARNING));
                    } else {
                        bsDepsRelativePath = "bs-platform/jscomp/runtime";
                    }
                }

                return new BsConfig(name, hasNamespace, bsDepsRelativePath, deps);
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

    @NotNull
    private static String toNamespace(@NotNull String name) {
        String result = name.replaceAll("_", "");

        String[] tokens = result.split("[-@/]");
        if (1 < tokens.length) {
            result = upperCaseFirst(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                result += upperCaseFirst(tokens[i]);
            }
        }

        return result;
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
