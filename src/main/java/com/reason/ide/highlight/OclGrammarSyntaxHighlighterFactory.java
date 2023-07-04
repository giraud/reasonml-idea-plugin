package com.reason.ide.highlight;

import com.intellij.lexer.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocamlgrammar.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.psi.TokenType.*;
import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public class OclGrammarSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    private static final Set<IElementType> KEYWORD_TYPES = of(
            OclGrammarTypes.INSTANCE.DECLARE, OclGrammarTypes.INSTANCE.PLUGIN, OclGrammarTypes.INSTANCE.GRAMMAR,
            OclGrammarTypes.INSTANCE.VERNAC, OclGrammarTypes.INSTANCE.EXTEND, OclGrammarTypes.INSTANCE.COMMAND,
            OclGrammarTypes.INSTANCE.TACTIC, OclGrammarTypes.INSTANCE.ARGUMENT, OclGrammarTypes.INSTANCE.END
    );

    private static final Set<IElementType> OPERATION_SIGNS = of(
            OclGrammarTypes.INSTANCE.PIPE, OclGrammarTypes.INSTANCE.ARROW
    );

    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new SyntaxHighlighterBase() {
            private final OclGrammarTypes myTypes = OclGrammarTypes.INSTANCE;

            @Override
            public @NotNull Lexer getHighlightingLexer() {
                return new FlexAdapter(new OclGrammarLexer());
            }

            @Override
            public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
                if (tokenType == myTypes.SINGLE_COMMENT || tokenType == myTypes.MULTI_COMMENT) {
                    return COMMENT_KEYS;
                }
                if (tokenType == BAD_CHARACTER) {
                    return BAD_CHAR_KEYS;
                }
                if (tokenType == myTypes.STRING_VALUE) {
                    return STRING_KEYS;
                }
                if (tokenType == myTypes.LBRACE || tokenType == myTypes.RBRACE) {
                    return BRACE_KEYS;
                }
                if (tokenType == myTypes.LBRACKET || tokenType == myTypes.RBRACKET) {
                    return BRACKET_KEYS;
                }
                if (tokenType == myTypes.LPAREN || tokenType == myTypes.RPAREN) {
                    return PAREN_KEYS;
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
