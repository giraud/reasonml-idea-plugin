package com.reason.comp;

import com.reason.ide.annotations.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public interface CompilerOutputAnalyzer {
    Pattern FILE_LOCATION = Pattern.compile("\\s*File \"(.+)\", lines? (\\d+)(?:-(\\d+))?, characters (\\d+)-(\\d+):");
    Pattern SYNTAX_LOCATION = Pattern.compile("\\s*(.+):(\\d+):(\\d+)(?:-(\\d+)(?::(\\d+))?)?");

    @NotNull List<OutputInfo> getOutputInfo();

    void onTextAvailable(@NotNull String line);
}
