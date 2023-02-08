package com.reason.hints;

import org.jetbrains.annotations.*;

import java.util.regex.*;

public class Rincewind {
    private static final Pattern OCAML_VERSION_REGEXP = Pattern.compile(".*OCaml[:]?(\\d\\.\\d+).\\d+.*\\)");

    private Rincewind() {
    }

    static @Nullable String extractOcamlVersion(@Nullable String fullVersion) {
        if (fullVersion != null) {
            Matcher matcher = OCAML_VERSION_REGEXP.matcher(fullVersion);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            if (fullVersion.startsWith("ReScript")) {
                return "4.06";
            }
        }

        return null;
    }

    static @NotNull String getLatestVersion(@Nullable String ocamlVersion) {
        if ("4.02".equals(ocamlVersion)) {
            return "0.4";
        }

        return "0.10";
    }
}
