package com.reason.ide.console.dune;

import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.util.regex.*;

import static java.lang.Integer.*;

/**
 * Filter consoles output to add hyperlink to file reference.
 * <p>
 * OCaml (from https://github.com/Chris00/tuareg/blob/master/compilation.txt)
 * File "xxx.ml", line x, characters x-y:
 * File "xxx.ml", lines x-y, characters x-y:
 */
public class OCamlConsoleFilter implements Filter {
    private static final Pattern OCAML_PATTERN = Pattern.compile("^File \"(.+\\.(?:ml|re)i?)\", lines? (\\d+)(?:-(\\d+))?, characters (\\d+)-(\\d+):$");
    private final Project myProject;

    public OCamlConsoleFilter(Project project) {
        myProject = project;
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
        Matcher matcher = OCAML_PATTERN.matcher(line);
        if (matcher.find()) {
            String filePath = matcher.group(1);
            VirtualFile duneRoot = ORProjectManager.findFirstDuneContentRoot(myProject);
            VirtualFile virtualFile = duneRoot == null ? null : duneRoot.findFileByRelativePath(filePath);
            if (virtualFile != null) {
                boolean multiline = matcher.groupCount() == 5;
                int startPoint = entireLength - line.length();
                int documentLine = parseInt(matcher.group(2)) - 1;
                int documentColumn = parseInt(matcher.group(multiline ? 4 : 3));
                return new Result(startPoint + matcher.start(1), startPoint + matcher.end(1),
                        new OpenFileHyperlinkInfo(myProject, virtualFile, documentLine, documentColumn));
            }
        }

        return null;
    }
}
