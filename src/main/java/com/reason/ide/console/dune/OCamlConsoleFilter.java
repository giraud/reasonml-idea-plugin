package com.reason.ide.console.dune;

import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import com.reason.comp.dune.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

import java.util.regex.*;

import static java.lang.Integer.*;

/**
 * Filter consoles output to add hyperlink to file reference.
 * <p>
 * OCaml (from <a href="https://github.com/Chris00/tuareg/blob/master/compilation.txt">https://github.com/Chris00/tuareg/blob/master/compilation.txt</a>)
 * File "xxx.ml", line x, characters x-y:
 * File "xxx.ml", lines x-y, characters x-y:
 */
public class OCamlConsoleFilter extends ORConsoleFilter {
    private static final Pattern OCAML_PATTERN = Pattern.compile("^File \"(.+\\.(?:ml|re)i?)\", lines? (\\d+)(?:-(\\d+))?, characters (\\d+)-(\\d+):$");

    public OCamlConsoleFilter(@NotNull Project project) {
        super(project);
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
        Matcher matcher = OCAML_PATTERN.matcher(line);
        if (matcher.find()) {
            try {
                boolean multiline = matcher.groupCount() == 5;
                int documentLine = parseInt(matcher.group(2)) - 1;
                int documentColumn = parseInt(matcher.group(multiline ? 4 : 3));

                OpenFileHyperlinkInfo hyperlinkInfo = getHyperlinkInfo(matcher.group(1), documentLine, documentColumn);
                int startPoint = entireLength - line.length();
                int highlightStartOffset = startPoint + matcher.start(1);
                int highlightEndOffset = startPoint + matcher.end(1);

                return new Result(highlightStartOffset, highlightEndOffset, hyperlinkInfo);
            } catch (NumberFormatException e) {
                LOG.error("Format exception for line [" + line + "]", e);
            }
        }

        return null;
    }

    @Override
    protected @Nullable OpenFileHyperlinkInfo getHyperlinkInfo(String filePath, int documentLine, int documentColumn) {
        return DunePlatform.findConfigFiles(myProject).stream()
                .findFirst()
                .map(configFile -> configFile.getParent().findFileByRelativePath(filePath))
                .map(virtualFile -> new OpenFileHyperlinkInfo(myProject, virtualFile, documentLine, documentColumn))
                .orElse(null);
    }
}
