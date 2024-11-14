package com.reason.ide.highlight;

import com.intellij.lexer.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.ex.util.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.ocamlyacc.*;
import org.jetbrains.annotations.*;

public final class OclYaccEditorHighlighter extends LayeredLexerEditorHighlighter {
    public OclYaccEditorHighlighter(@Nullable Project project, @Nullable VirtualFile file, @NotNull EditorColorsScheme colors) {
        super(new OclYaccSyntaxHighlighterFactory().getSyntaxHighlighter(project, file), colors);

        SyntaxHighlighter oclSyntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(OclFileType.INSTANCE, project, file);
        if (oclSyntaxHighlighter != null) {
            registerLayer(OclYaccTypes.INSTANCE.TEMPLATE_OCAML_TEXT, new LayerDescriptor(new SyntaxHighlighter() {
                @Override
                public @NotNull Lexer getHighlightingLexer() {
                    return oclSyntaxHighlighter.getHighlightingLexer();
                }

                @Override
                public @NotNull TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
                    return oclSyntaxHighlighter.getTokenHighlights(tokenType);
                }
            }, ""));
        }
    }
}
