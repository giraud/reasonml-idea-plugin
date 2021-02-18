package com.reason.ide.highlight;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.lang.napkin.ResTypes;
import org.jetbrains.annotations.*;

public class ResSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new ORSyntaxHighlighter(ResTypes.INSTANCE);
    }
}
