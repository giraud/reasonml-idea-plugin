package com.reason.ide.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.ReasonMLLexerAdapter;
import com.reason.psi.ReasonMLTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.reason.psi.ReasonMLTypes.*;

public class ReasonMLSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Set<IElementType> KEYWORD_TYPES = of(MODULE, FUN, LET, TYPE, INCLUDE);
    private static final Set<IElementType> OPERATION_SIGN_TYPES = of(EQUAL, ARROW, SHORTCUT, COLON);

    public static final TextAttributesKey COMMENT_ = createTextAttributesKey("REASONML_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey STRING_ = createTextAttributesKey("REASONML_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey TAG = createTextAttributesKey("REASONML_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MODULE_NAME_ = createTextAttributesKey("REASONML_UIDENT", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey KEYWORD = createTextAttributesKey("REASONML_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SEMICOLON = createTextAttributesKey("REASONML_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey BRACKETS = createTextAttributesKey("REASONML_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey BRACES = createTextAttributesKey("REASONML_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey PARENS = createTextAttributesKey("REASONML_PARENS", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey OPERATION_SIGN = createTextAttributesKey("REASONML_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey BAD_CHAR = createTextAttributesKey("REASONML_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT_};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING_};
    private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACES};
    private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PARENS};
    public static final TextAttributesKey[] MODULE_NAME_KEYS = new TextAttributesKey[]{MODULE_NAME_};
    private static final TextAttributesKey[] TAG_KEYS = new TextAttributesKey[]{TAG};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON};
    private static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{OPERATION_SIGN};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new ReasonMLLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(LBRACE) || tokenType.equals(RBRACE)) {
            return BRACE_KEYS;
        } else if (tokenType.equals(LPAREN) || tokenType.equals(RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(AUTO_CLOSE_TAG) || tokenType.equals(CLOSE_TAG) || tokenType.equals(GT) || tokenType.equals(LT)) {
            return TAG_KEYS;
        } else if (SEMI.equals(tokenType)) {
            return SEMICOLON_KEYS;
        } else if (STRING.equals(tokenType)) {
            return STRING_KEYS;
        } else if (KEYWORD_TYPES.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (OPERATION_SIGN_TYPES.contains(tokenType)) {
            return OPERATION_SIGN_KEYS;
        } else if (BAD_CHARACTER.equals(tokenType)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

    private static Set<IElementType> of(IElementType... types) {
        Set<IElementType> result = new HashSet<>();
        for (IElementType type : types) {
            result.add(type);
        }
        return result;
    }
}
