package com.reason.ide.highlight;

import com.intellij.lexer.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.tree.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.*;
import static com.intellij.psi.TokenType.*;

public class DuneSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey PARENS_ =
            createTextAttributesKey("DUNE_PAREN", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey COMMENT_ =
            createTextAttributesKey("DUNE_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey STANZAS_ =
            createTextAttributesKey("DUNE_STANZA", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey FIELDS_ =
            createTextAttributesKey("DUNE_FIELD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OPTIONS_ =
            createTextAttributesKey("DUNE_OPTION", DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey STRING_ =
            createTextAttributesKey("DUNE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey ATOM_ =
            createTextAttributesKey("DUNE_ATOM", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey VAR_ =
            createTextAttributesKey("DUNE_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    private static final TextAttributesKey BAD_CHAR_ =
            createTextAttributesKey("DUNE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT_};
    private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PARENS_};
    private static final TextAttributesKey[] STANZAS_KEYS = new TextAttributesKey[]{STANZAS_};
    private static final TextAttributesKey[] FIELDS_KEYS = new TextAttributesKey[]{FIELDS_};
    private static final TextAttributesKey[] OPTIONS_KEYS = new TextAttributesKey[]{OPTIONS_};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING_};
    private static final TextAttributesKey[] ATOM_KEYS = new TextAttributesKey[]{ATOM_};
    private static final TextAttributesKey[] VAR_KEYS = new TextAttributesKey[]{VAR_};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR_};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FlexAdapter(new DuneLexer(DuneTypes.INSTANCE));
    }

    @Override
    public @NotNull TextAttributesKey[] getTokenHighlights(@NotNull IElementType tokenType) {
        if (tokenType.equals(DuneTypes.INSTANCE.LPAREN) || tokenType.equals(DuneTypes.INSTANCE.RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(DuneTypes.INSTANCE.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(DuneTypes.INSTANCE.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(DuneTypes.INSTANCE.ATOM)) {
            return ATOM_KEYS;
        } else if (BAD_CHARACTER.equals(tokenType)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }
}
