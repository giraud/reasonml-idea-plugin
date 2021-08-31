package com.reason.comp;

import com.reason.ide.annotations.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public interface CompilerOutputAnalyzer {
    Pattern FILE_LOCATION = Pattern.compile("File \"(.+)\", line (\\d+), characters (\\d+)-(\\d+):");
    Pattern SYNTAX_LOCATION = Pattern.compile("(.+):(\\d+):(\\d+)(?:-(\\d+))?");

    List<OutputInfo> getOutputInfo();

    void onTextAvailable(@NotNull String line);
}
