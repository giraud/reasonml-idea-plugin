package com.reason.ide.hints;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.annotations.ErrorsManager;
import com.reason.ide.highlight.ORSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class OREditorLinePainter extends EditorLinePainter {

    @Nullable
    @Override
    public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        //long start = System.currentTimeMillis();

        CodeLensView.CodeLensInfo data = project.getUserData(CodeLensView.CODE_LENS);
        if (data == null) {
            return null;
        }

        if (ServiceManager.getService(project, ErrorsManager.class).hasErrors(file.getName(), lineNumber + 1)) {
            return null;
        }

        Collection<LineExtensionInfo> info = null;

        String signature = data.get(file, lineNumber);
        if (signature != null) {
            EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
            TextAttributes codeLens = globalScheme.getAttributes(ORSyntaxHighlighter.CODE_LENS_);
            info = Collections.singletonList(new LineExtensionInfo("  " + signature, codeLens));
        }

        //long end = System.currentTimeMillis();
        //System.out.println("line extensions in " + (end - start) + "ms");
        return info;
    }
}
