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

    // File "...path/src/Source.ml", line 111, characters 0-3:
    protected @Nullable OutputInfo extractExtendedFilePositions(@NotNull Log LOG, @NotNull String line) {
        Matcher matcher = FILE_LOCATION.matcher(line);
        if (matcher.matches()) {
            String path = matcher.group(1);
            String linePos = matcher.group(2);
            String colStart = matcher.group(3);
            String colEnd = matcher.group(4);
            OutputInfo info = addInfo(path, linePos, colStart, colEnd);
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
            String linePos = matcher.group(2);
            String colStart = matcher.group(3);
            String colEnd = matcher.group(4);
            OutputInfo info = addInfo(path, linePos, colStart, colEnd);
            if (info.colStart < 0 || info.colEnd < 0) {
                LOG.error("Can't decode columns for [" + line.replace("\n", "") + "]");
                return null;
            }
            return info;
        }

        return null;
    }

    protected @NotNull OutputInfo addInfo(@NotNull String path, @NotNull String line, @NotNull String colStart, @Nullable String colEnd) {
        OutputInfo info = new OutputInfo();

        info.path = path;
        info.lineStart = parseInt(line);
        info.colStart = parseInt(colStart);
        info.lineEnd = info.lineStart;
        info.colEnd = colEnd == null ? info.colStart : parseInt(colEnd);
        if (info.colEnd == info.colStart) {
            info.colEnd += 1;
        }

        myOutputInfo.add(info);
        return info;
    }
}
