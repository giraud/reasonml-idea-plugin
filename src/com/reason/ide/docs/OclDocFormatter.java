package com.reason.ide.docs;

import org.jetbrains.annotations.NotNull;

public class OclDocFormatter {
    public static String format(@NotNull String text) {
        // Parse odoc special comment with a grammar ?
        String substring = text.substring(3); // remove (**_
        return substring.substring(0, substring.length() - 3); // remove *)
    }
}
