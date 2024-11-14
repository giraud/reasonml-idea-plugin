package com.reason.ide.highlight;

import com.intellij.lexer.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocamllex.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public class OclLexSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    private static final Set<IElementType> KEYWORD_TYPES = of(OclLexTypes.INSTANCE.LET, OclLexTypes.INSTANCE.RULE,
            OclLexTypes.INSTANCE.PARSE, OclLexTypes.INSTANCE.AND);

    private static final Set<IElementType> OPERATION_SIGNS = of(OclLexTypes.INSTANCE.PIPE, OclLexTypes.INSTANCE.DASH,
            OclLexTypes.INSTANCE.LBRACE, OclLexTypes.INSTANCE.RBRACE, OclLexTypes.INSTANCE.LBRACKET, OclLexTypes.INSTANCE.RBRACKET,
            OclLexTypes.INSTANCE.EQ);

    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new SyntaxHighlighterBase() {
            private final OclLexTypes myTypes = OclLexTypes.INSTANCE;

            @Override
            public @NotNull Lexer getHighlightingLexer() {
                return new FlexAdapter(new OclLexLexer());
            }

            @Override
            public @NotNull TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
                if (myTypes.SINGLE_COMMENT.equals(tokenType)) {
                    return ORSyntaxHighlighter.COMMENT_KEYS;
                }
                if (myTypes.STRING_VALUE.equals(tokenType)) {
                    return STRING_KEYS;
                }
                if (BAD_CHARACTER.equals(tokenType)) {
                    return BAD_CHAR_KEYS;
                }

                if (myTypes.LBRACE.equals(tokenType) || myTypes.RBRACE.equals(tokenType)) {
                    return BRACE_KEYS;
                } else if (myTypes.LBRACKET.equals(tokenType) || myTypes.RBRACKET.equals(tokenType)) {
                    return BRACKET_KEYS;
                }

                if (KEYWORD_TYPES.contains(tokenType)) {
                    return KEYWORD_KEYS;
                }
                if (OPERATION_SIGNS.contains(tokenType)) {
                    return OPERATION_SIGN_KEYS;
                }

                return TextAttributesKey.EMPTY_ARRAY;
            }
        };
    }
}
