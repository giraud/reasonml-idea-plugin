package com.reason.ide.highlight;

import com.intellij.lexer.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.openapi.editor.colors.TextAttributesKey.*;
import static com.intellij.psi.TokenType.*;

public class ORSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final TextAttributesKey TYPE_ARGUMENT_KEY = TextAttributesKey.createTextAttributesKey("TYPE_ARGUMENT");

    public static final TextAttributesKey ANNOTATION_ = createTextAttributesKey("REASONML_ANNOTATION", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey BRACES_ = createTextAttributesKey("REASONML_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey BRACKETS_ = createTextAttributesKey("REASONML_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey CODE_LENS_ = createTextAttributesKey("REASONML_CODE_LENS", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey KEYWORD_ = createTextAttributesKey("REASONML_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey MACRO_ = createTextAttributesKey("REASONML_MACRO", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey INTERPOLATED_REF_ = createTextAttributesKey("REASONML_INTERPOLATED_REF", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey MARKUP_TAG_ = createTextAttributesKey("REASONML_MARKUP_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MARKUP_ATTRIBUTE_ = createTextAttributesKey("REASONML_MARKUP_ATTRIBUTE", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
    public static final TextAttributesKey MODULE_NAME_ = createTextAttributesKey("REASONML_MODULE_NAME", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey NUMBER_ = createTextAttributesKey("REASONML_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey OPERATION_SIGN_ = createTextAttributesKey("REASONML_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey OPTION_ = createTextAttributesKey("REASONML_OPTION", DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey PARENS_ = createTextAttributesKey("REASONML_PARENS", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey POLY_VARIANT_ = createTextAttributesKey("REASONML_POLY_VARIANT", DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey RML_COMMENT_ = createTextAttributesKey("REASONML_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey SEMICOLON_ = createTextAttributesKey("REASONML_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey STRING_ = createTextAttributesKey("REASONML_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey TYPE_ARGUMENT_ = createTextAttributesKey("REASONML_TYPE_ARGUMENT", TYPE_ARGUMENT_KEY);
    public static final TextAttributesKey VARIANT_NAME_ = createTextAttributesKey("REASONML_VARIANT_NAME", DefaultLanguageHighlighterColors.STATIC_FIELD);
    private static final TextAttributesKey BAD_CHAR_ = createTextAttributesKey("REASONML_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER_};
    static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{RML_COMMENT_};
    static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING_};
    private static final TextAttributesKey[] TYPE_ARGUMENT_KEYS = new TextAttributesKey[]{TYPE_ARGUMENT_};
    private static final TextAttributesKey[] POLY_VARIANT_KEYS = new TextAttributesKey[]{POLY_VARIANT_};
    static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKETS_};
    static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACES_};
    static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PARENS_};
    private static final TextAttributesKey[] OPTION_KEYS = new TextAttributesKey[]{OPTION_};
    static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD_};
    static final TextAttributesKey[] MACRO_KEYS = new TextAttributesKey[]{MACRO_};
    static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON_};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{OPERATION_SIGN_};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{OPERATION_SIGN_};
    private static final TextAttributesKey[] MARKUP_TAG_KEYS = new TextAttributesKey[]{MARKUP_TAG_};
    static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{OPERATION_SIGN_};
    static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR_};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    private final ORLangTypes myTypes;
    private final Set<IElementType> myKeywordTypes;
    private final Set<IElementType> myOperationSignTypes;
    private final Set<IElementType> myOptionTypes;
    private final Set<IElementType> myMacroTypes;

    public ORSyntaxHighlighter(ORLangTypes types, Set<IElementType> keywordTypes, Set<IElementType> operationSignTypes, Set<IElementType> optionTypes, Set<IElementType> macroTypes) {
        myTypes = types;
        myKeywordTypes = keywordTypes;
        myOperationSignTypes = operationSignTypes;
        myOptionTypes = optionTypes;
        myMacroTypes = macroTypes;
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return myTypes instanceof RmlTypes
                ? new RmlLexer()
                : myTypes instanceof ResTypes ? new ResLexer() : new OclLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(@NotNull IElementType tokenType) {
        if (tokenType.equals(myTypes.MULTI_COMMENT) || tokenType.equals(myTypes.SINGLE_COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(myTypes.LBRACE) || tokenType.equals(myTypes.RBRACE)) {
            return BRACE_KEYS;
        } else if (tokenType.equals(myTypes.LBRACKET)
                || tokenType.equals(myTypes.RBRACKET)
                || tokenType.equals(myTypes.LARRAY)
                || tokenType.equals(myTypes.RARRAY)
                || tokenType.equals(myTypes.ML_STRING_OPEN)
                || tokenType.equals(myTypes.ML_STRING_CLOSE)
                || tokenType.equals(myTypes.JS_STRING_OPEN)
                || tokenType.equals(myTypes.JS_STRING_CLOSE)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(myTypes.LPAREN) || tokenType.equals(myTypes.RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(myTypes.INT_VALUE) || tokenType.equals(myTypes.FLOAT_VALUE)) {
            return NUMBER_KEYS;
        } else if (myTypes.DOT.equals(tokenType)) {
            return DOT_KEYS;
        } else if (myTypes.TYPE_ARGUMENT.equals(tokenType)) {
            return TYPE_ARGUMENT_KEYS;
        } else if (myTypes.POLY_VARIANT.equals(tokenType)) {
            return POLY_VARIANT_KEYS;
        } else if (myTypes.COMMA.equals(tokenType)) {
            return COMMA_KEYS;
        } else if (myTypes.TAG_AUTO_CLOSE.equals(tokenType) || myTypes.TAG_LT_SLASH.equals(tokenType) || myTypes.A_LOWER_TAG_NAME.equals(tokenType) || myTypes.A_UPPER_TAG_NAME.equals(tokenType)) {
            return MARKUP_TAG_KEYS;
        } else if (myTypes.SEMI.equals(tokenType) || myTypes.SEMISEMI.equals(tokenType)) {
            return SEMICOLON_KEYS;
        } else if (myTypes.STRING_VALUE.equals(tokenType) || myTypes.CHAR_VALUE.equals(tokenType)) {
            return STRING_KEYS;
        } else if (myKeywordTypes.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (myOperationSignTypes.contains(tokenType)) {
            return OPERATION_SIGN_KEYS;
        } else if (myOptionTypes.contains(tokenType)) {
            return OPTION_KEYS;
        } else if (myMacroTypes.contains(tokenType)) {
            return MACRO_KEYS;
        } else if (BAD_CHARACTER.equals(tokenType)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

    @NotNull
    static Set<IElementType> of(IElementType... types) {
        Set<IElementType> result = new HashSet<>();
        Collections.addAll(result, types);
        return result;
    }
}
