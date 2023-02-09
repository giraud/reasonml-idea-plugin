package com.reason.ide.console.rescript;

import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
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
public class RescriptOCamlConsoleFilter implements Filter {
    private final Project myProject;

    public RescriptOCamlConsoleFilter(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
        Pattern pattern = line.trim().startsWith("File") ? FILE_LOCATION : SYNTAX_LOCATION;
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String filePath = matcher.group(1);
            VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(filePath);
            if (sourceFile != null) {
                boolean multiline = matcher.groupCount() == 6;
                int startPoint = entireLength - line.length();
                int documentLine = parseInt(matcher.group(2)) - 1;
                int documentColumn = parseInt(matcher.group(multiline ? 4 : 3));
                return new Result(startPoint + matcher.start(1), startPoint + matcher.end(1),
                        new OpenFileHyperlinkInfo(myProject, sourceFile, documentLine, documentColumn));
            }
        }

        return null;
    }
}
