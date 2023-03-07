package com.reason.comp;

import com.reason.ide.annotations.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

import static java.lang.Integer.*;

public abstract class ORCompilerOutputAnalyzer implements CompilerOutputAnalyzer {
    private final List<OutputInfo> myOutputInfo = new ArrayList<>();
    protected @Nullable OutputInfo myCurrentInfo = null;

    @Override
    public @NotNull List<OutputInfo> getOutputInfo() {
        return myOutputInfo;
    }

    @Override
    public abstract void onTextAvailable(@NotNull String line);

    protected @Nullable OutputInfo extractExtendedFilePositions(@NotNull Log LOG, @NotNull String line) {
        Matcher matcher = FILE_LOCATION.matcher(line);
        if (matcher.matches()) {
            String path = matcher.group(1);
            String lineStart = matcher.group(2);
            String lineEnd = matcher.group(3);
            String colStart = matcher.group(4);
            String colEnd = matcher.group(5);
            OutputInfo info = addInfo(path, lineStart, lineEnd, colStart, colEnd);
            if (info.colStart < 0 || info.colEnd < 0) {
                LOG.error("Can't decode columns for [" + line.replace("\n", "") + "]");
                return null;
            }
            return info;
        }

        return null;
    }

    //  C:\bla\bla\src\InputTest.res:1:11(-12)?
    protected @Nullable OutputInfo extractSyntaxErrorFilePosition(@NotNull Log LOG, @NotNull String line) {
        Matcher matcher = SYNTAX_LOCATION.matcher(line);
        if (matcher.matches()) {
            String path = matcher.group(1);
            String lineStart = matcher.group(2);
            String colStart = matcher.group(3);
            String colEnd = matcher.group(4);
            OutputInfo info = addInfo(path, lineStart, null, colStart, colEnd);
            if (info.colStart < 0 || info.colEnd < 0) {
                LOG.error("Can't decode columns for [" + line.replace("\n", "") + "]");
                return null;
            }
            return info;
        }

        return null;
    }

    protected @NotNull OutputInfo addInfo(@NotNull String path, @NotNull String lineStart, @Nullable String lineEnd, @NotNull String colStart, @Nullable String colEnd) {
        OutputInfo info = new OutputInfo();

        info.path = path;
        info.lineStart = parseInt(lineStart);
        info.colStart = parseInt(colStart);
        info.lineEnd = lineEnd == null ? info.lineStart : parseInt(lineEnd);
        info.colEnd = colEnd == null ? info.colStart : parseInt(colEnd);
        if (info.colEnd == info.colStart) {
            info.colEnd += 1;
        }

        myOutputInfo.add(info);
        return info;
    }
}
