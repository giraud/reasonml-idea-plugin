package com.reason.ide.console.rescript;

import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

import java.util.regex.*;

import static com.reason.comp.CompilerOutputAnalyzer.*;
import static java.lang.Integer.*;

/**
 * Filter consoles output to add hyperlink to file reference.
 * <p>
 * OCaml (from <a href="https://github.com/Chris00/tuareg/blob/master/compilation.txt">https://github.com/Chris00/tuareg/blob/master/compilation.txt</a>)
 * File "xxx.ml", line x, characters x-y:
 * File "xxx.ml", lines x-y, characters x-y:
 */
public class RescriptConsoleFilter extends ORConsoleFilter {

    public RescriptConsoleFilter(@NotNull Project project) {
        super(project);
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
        Pattern pattern = line.trim().startsWith("File") ? FILE_LOCATION : SYNTAX_LOCATION;
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            try {
                boolean multiline = matcher.groupCount() >= 5;
                int startPoint = entireLength - line.length();
                int documentLine = parseInt(matcher.group(2)) - 1;
                int documentColumn = parseInt(matcher.group(multiline ? 4 : 3));
                OpenFileHyperlinkInfo hyperlinkInfo = getHyperlinkInfo(matcher.group(1), documentLine, documentColumn);

                return new Result(startPoint + matcher.start(1), startPoint + matcher.end(1), hyperlinkInfo);
            } catch (NumberFormatException e) {
                LOG.error("Format exception for line [" + line + "]", e);
            }
        }

        return null;
    }

    @Override
    protected @Nullable OpenFileHyperlinkInfo getHyperlinkInfo(String filePath, int documentLine, int documentColumn) {
        OpenFileHyperlinkInfo hyperlinkInfo = null;
        VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        if (sourceFile != null) {
            hyperlinkInfo = new OpenFileHyperlinkInfo(myProject, sourceFile, documentLine, documentColumn);
        }
        return hyperlinkInfo;
    }
}
