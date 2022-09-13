package com.reason.ide.hints;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.highlight.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.ide.hints.CodeLens.*;

public class OREditorLinePainter extends EditorLinePainter {
    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        Collection<LineExtensionInfo> result = null;

        CodeLens signatures = file.getUserData(CODE_LENS);
        if (signatures != null) {
            String signature = signatures.get(lineNumber);
            if (signature != null) {
                EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
                TextAttributes codeLens = globalScheme.getAttributes(ORSyntaxHighlighter.CODE_LENS_);
                result = Collections.singletonList(new LineExtensionInfo("  " + signature, codeLens));
            }
        }

        return result;
    }
}
