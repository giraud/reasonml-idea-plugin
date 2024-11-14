package com.reason.ide.highlight;

import com.intellij.lexer.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocamlyacc.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.psi.TokenType.*;
import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public class OclYaccSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    private static final Set<IElementType> KEYWORD_TYPES = of(
            OclYaccTypes.INSTANCE.TOKEN, OclYaccTypes.INSTANCE.TYPE, OclYaccTypes.INSTANCE.START,
            OclYaccTypes.INSTANCE.LEFT, OclYaccTypes.INSTANCE.RIGHT, OclYaccTypes.INSTANCE.NON_ASSOC,
            OclYaccTypes.INSTANCE.INLINE
    );

    private static final Set<IElementType> OPERATION_SIGNS = of(
            OclYaccTypes.INSTANCE.HEADER_START, OclYaccTypes.INSTANCE.HEADER_STOP, OclYaccTypes.INSTANCE.SECTION_SEPARATOR,
            OclYaccTypes.INSTANCE.COLON, OclYaccTypes.INSTANCE.PIPE
    );

    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new SyntaxHighlighterBase() {
            private final OclYaccTypes myTypes = OclYaccTypes.INSTANCE;

            @Override
            public @NotNull Lexer getHighlightingLexer() {
                return new FlexAdapter(new OclYaccLexer());
            }

            @Override
            public @NotNull TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
                if (tokenType == myTypes.SINGLE_COMMENT || tokenType == myTypes.MULTI_COMMENT) {
                    return COMMENT_KEYS;
                }
                if (tokenType == BAD_CHARACTER) {
                    return BAD_CHAR_KEYS;
                }
                if (tokenType == myTypes.SEMI) {
                    return SEMICOLON_KEYS;
                }
                if (tokenType == myTypes.LBRACE || tokenType == myTypes.RBRACE) {
                    return BRACE_KEYS;
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
