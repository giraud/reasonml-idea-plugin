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

    private static final Set<IElementType> RML_KEYWORD_TYPES =
            of(RmlTypes.INSTANCE.OPEN, RmlTypes.INSTANCE.MODULE, RmlTypes.INSTANCE.FUN, RmlTypes.INSTANCE.LET, RmlTypes.INSTANCE.TYPE,
                    RmlTypes.INSTANCE.INCLUDE, RmlTypes.INSTANCE.EXTERNAL, RmlTypes.INSTANCE.IF, RmlTypes.INSTANCE.ELSE,
                    RmlTypes.INSTANCE.SWITCH, RmlTypes.INSTANCE.TRY, RmlTypes.INSTANCE.RAISE,
                    RmlTypes.INSTANCE.FOR, RmlTypes.INSTANCE.IN, RmlTypes.INSTANCE.TO, RmlTypes.INSTANCE.BOOL_VALUE,
                    RmlTypes.INSTANCE.REF, RmlTypes.INSTANCE.EXCEPTION, RmlTypes.INSTANCE.WHEN, RmlTypes.INSTANCE.AND,
                    RmlTypes.INSTANCE.REC, RmlTypes.INSTANCE.WHILE, RmlTypes.INSTANCE.ASR, RmlTypes.INSTANCE.CLASS,
                    RmlTypes.INSTANCE.CONSTRAINT, RmlTypes.INSTANCE.DOWNTO, RmlTypes.INSTANCE.FUNCTOR, RmlTypes.INSTANCE.INHERIT,
                    RmlTypes.INSTANCE.INITIALIZER, RmlTypes.INSTANCE.LAND, RmlTypes.INSTANCE.LOR, RmlTypes.INSTANCE.LSL,
                    RmlTypes.INSTANCE.LSR, RmlTypes.INSTANCE.LXOR, RmlTypes.INSTANCE.METHOD, RmlTypes.INSTANCE.MOD,
                    RmlTypes.INSTANCE.NEW, RmlTypes.INSTANCE.NONREC, RmlTypes.INSTANCE.OR, RmlTypes.INSTANCE.PRIVATE,
                    RmlTypes.INSTANCE.VIRTUAL, RmlTypes.INSTANCE.VAL, RmlTypes.INSTANCE.PUB, RmlTypes.INSTANCE.PRI,
                    RmlTypes.INSTANCE.OBJECT, RmlTypes.INSTANCE.MUTABLE, RmlTypes.INSTANCE.UNIT, RmlTypes.INSTANCE.WITH,
                    RmlTypes.INSTANCE.DIRECTIVE_IF, RmlTypes.INSTANCE.DIRECTIVE_ELSE, RmlTypes.INSTANCE.DIRECTIVE_ELIF,
                    RmlTypes.INSTANCE.DIRECTIVE_END, RmlTypes.INSTANCE.DIRECTIVE_ENDIF);

    private static final Set<IElementType> RML_OPERATION_SIGN_TYPES =
            of(
                    RmlTypes.INSTANCE.L_AND, RmlTypes.INSTANCE.L_OR, RmlTypes.INSTANCE.SHORTCUT, RmlTypes.INSTANCE.ARROW,
                    RmlTypes.INSTANCE.PIPE_FORWARD, RmlTypes.INSTANCE.EQEQEQ, RmlTypes.INSTANCE.EQEQ,
                    RmlTypes.INSTANCE.EQ, RmlTypes.INSTANCE.NOT_EQEQ, RmlTypes.INSTANCE.NOT_EQ, RmlTypes.INSTANCE.DIFF,
                    RmlTypes.INSTANCE.COLON, RmlTypes.INSTANCE.SINGLE_QUOTE, RmlTypes.INSTANCE.DOUBLE_QUOTE,
                    RmlTypes.INSTANCE.CARRET, RmlTypes.INSTANCE.PLUSDOT, RmlTypes.INSTANCE.MINUSDOT,
                    RmlTypes.INSTANCE.SLASHDOT, RmlTypes.INSTANCE.STARDOT, RmlTypes.INSTANCE.PLUS, RmlTypes.INSTANCE.MINUS,
                    RmlTypes.INSTANCE.SLASH, RmlTypes.INSTANCE.STAR, RmlTypes.INSTANCE.PERCENT, RmlTypes.INSTANCE.PIPE,
                    RmlTypes.INSTANCE.ARROBASE, RmlTypes.INSTANCE.SHARP, RmlTypes.INSTANCE.SHARPSHARP,
                    RmlTypes.INSTANCE.QUESTION_MARK, RmlTypes.INSTANCE.EXCLAMATION_MARK, RmlTypes.INSTANCE.LT_OR_EQUAL,
                    RmlTypes.INSTANCE.GT_OR_EQUAL, RmlTypes.INSTANCE.AMPERSAND, RmlTypes.INSTANCE.LEFT_ARROW,
                    RmlTypes.INSTANCE.RIGHT_ARROW, RmlTypes.INSTANCE.COLON_EQ, RmlTypes.INSTANCE.COLON_GT,
                    RmlTypes.INSTANCE.GT, RmlTypes.INSTANCE.GT_BRACE, RmlTypes.INSTANCE.GT_BRACKET, RmlTypes.INSTANCE.BRACKET_GT,
                    RmlTypes.INSTANCE.BRACKET_LT, RmlTypes.INSTANCE.BRACE_LT, RmlTypes.INSTANCE.DOTDOT, RmlTypes.INSTANCE.STRING_CONCAT);

    private static final Set<IElementType> RML_OPTIONS_TYPES =
            of(RmlTypes.INSTANCE.NONE, RmlTypes.INSTANCE.SOME);

    private static final Set<IElementType> RES_KEYWORD_TYPES =
            of(
                    ResTypes.INSTANCE.OPEN, ResTypes.INSTANCE.MODULE, ResTypes.INSTANCE.FUN, ResTypes.INSTANCE.LET,
                    ResTypes.INSTANCE.TYPE, ResTypes.INSTANCE.INCLUDE, ResTypes.INSTANCE.EXTERNAL, ResTypes.INSTANCE.LIST,
                    ResTypes.INSTANCE.IF, ResTypes.INSTANCE.ELSE, ResTypes.INSTANCE.SWITCH,
                    ResTypes.INSTANCE.TRY, ResTypes.INSTANCE.CATCH, ResTypes.INSTANCE.RAISE, ResTypes.INSTANCE.FOR, ResTypes.INSTANCE.IN,
                    ResTypes.INSTANCE.TO, ResTypes.INSTANCE.BOOL_VALUE, ResTypes.INSTANCE.REF, ResTypes.INSTANCE.EXCEPTION,
                    ResTypes.INSTANCE.WHEN, ResTypes.INSTANCE.AND, ResTypes.INSTANCE.REC, ResTypes.INSTANCE.WHILE,
                    ResTypes.INSTANCE.ASR, ResTypes.INSTANCE.CLASS, ResTypes.INSTANCE.CONSTRAINT, ResTypes.INSTANCE.DOWNTO,
                    ResTypes.INSTANCE.FUNCTOR, ResTypes.INSTANCE.INHERIT, ResTypes.INSTANCE.INITIALIZER,
                    ResTypes.INSTANCE.LAND, ResTypes.INSTANCE.LOR, ResTypes.INSTANCE.LSL, ResTypes.INSTANCE.LSR,
                    ResTypes.INSTANCE.LXOR, ResTypes.INSTANCE.METHOD, ResTypes.INSTANCE.MOD, ResTypes.INSTANCE.NEW,
                    ResTypes.INSTANCE.NONREC, ResTypes.INSTANCE.OR, ResTypes.INSTANCE.PRIVATE, ResTypes.INSTANCE.VIRTUAL,
                    ResTypes.INSTANCE.VAL, ResTypes.INSTANCE.PUB, ResTypes.INSTANCE.PRI, ResTypes.INSTANCE.OBJECT,
                    ResTypes.INSTANCE.MUTABLE, ResTypes.INSTANCE.UNIT, ResTypes.INSTANCE.WITH, ResTypes.INSTANCE.DIRECTIVE_IF,
                    ResTypes.INSTANCE.DIRECTIVE_ELSE, ResTypes.INSTANCE.DIRECTIVE_ELIF, ResTypes.INSTANCE.DIRECTIVE_END,
                    ResTypes.INSTANCE.DIRECTIVE_ENDIF, ResTypes.INSTANCE.UNPACK);

    private static final Set<IElementType> RES_OPERATION_SIGN_TYPES =
            of(
                    ResTypes.INSTANCE.L_AND, ResTypes.INSTANCE.L_OR, ResTypes.INSTANCE.SHORTCUT, ResTypes.INSTANCE.ARROW,
                    ResTypes.INSTANCE.PIPE_FORWARD, ResTypes.INSTANCE.EQEQEQ, ResTypes.INSTANCE.EQEQ, ResTypes.INSTANCE.EQ,
                    ResTypes.INSTANCE.NOT_EQEQ, ResTypes.INSTANCE.NOT_EQ, ResTypes.INSTANCE.DIFF, ResTypes.INSTANCE.COLON,
                    ResTypes.INSTANCE.SINGLE_QUOTE, ResTypes.INSTANCE.DOUBLE_QUOTE, ResTypes.INSTANCE.CARRET,
                    ResTypes.INSTANCE.PLUSDOT, ResTypes.INSTANCE.MINUSDOT, ResTypes.INSTANCE.SLASHDOT, ResTypes.INSTANCE.STARDOT,
                    ResTypes.INSTANCE.PLUS, ResTypes.INSTANCE.MINUS, ResTypes.INSTANCE.SLASH, ResTypes.INSTANCE.STAR,
                    ResTypes.INSTANCE.PERCENT, ResTypes.INSTANCE.PIPE, ResTypes.INSTANCE.ARROBASE, ResTypes.INSTANCE.SHARP,
                    ResTypes.INSTANCE.SHARPSHARP, ResTypes.INSTANCE.QUESTION_MARK, ResTypes.INSTANCE.EXCLAMATION_MARK,
                    ResTypes.INSTANCE.LT_OR_EQUAL, ResTypes.INSTANCE.GT_OR_EQUAL, ResTypes.INSTANCE.AMPERSAND,
                    ResTypes.INSTANCE.LEFT_ARROW, ResTypes.INSTANCE.RIGHT_ARROW, ResTypes.INSTANCE.COLON_EQ, ResTypes.INSTANCE.COLON_GT,
                    ResTypes.INSTANCE.LT, ResTypes.INSTANCE.GT, ResTypes.INSTANCE.GT_BRACE, ResTypes.INSTANCE.GT_BRACKET,
                    ResTypes.INSTANCE.BRACKET_GT, ResTypes.INSTANCE.BRACKET_LT, ResTypes.INSTANCE.BRACE_LT,
                    ResTypes.INSTANCE.DOTDOT, ResTypes.INSTANCE.STRING_CONCAT);

    private static final Set<IElementType> RES_OPTIONS_TYPES =
            of(ResTypes.INSTANCE.NONE, ResTypes.INSTANCE.SOME);

    private static final Set<IElementType> OCL_KEYWORD_TYPES =
            of(
                    // reserved
                    OclTypes.INSTANCE.ASSERT, OclTypes.INSTANCE.AND, OclTypes.INSTANCE.ASR, OclTypes.INSTANCE.AS,
                    OclTypes.INSTANCE.BEGIN, OclTypes.INSTANCE.CLASS, OclTypes.INSTANCE.CONSTRAINT, OclTypes.INSTANCE.DOWNTO,
                    OclTypes.INSTANCE.DONE, OclTypes.INSTANCE.DO, OclTypes.INSTANCE.EXCEPTION, OclTypes.INSTANCE.EXTERNAL,
                    OclTypes.INSTANCE.ELSE, OclTypes.INSTANCE.END, OclTypes.INSTANCE.FUNCTION, OclTypes.INSTANCE.FUNCTOR,
                    OclTypes.INSTANCE.BOOL_VALUE/*false*/, OclTypes.INSTANCE.FUN, OclTypes.INSTANCE.FOR,
                    OclTypes.INSTANCE.INITIALIZER, OclTypes.INSTANCE.INCLUDE, OclTypes.INSTANCE.INHERIT,
                    OclTypes.INSTANCE.IF, OclTypes.INSTANCE.IN, OclTypes.INSTANCE.LAND, OclTypes.INSTANCE.LAZY,
                    OclTypes.INSTANCE.LXOR, OclTypes.INSTANCE.LET, OclTypes.INSTANCE.LOR, OclTypes.INSTANCE.LSL,
                    OclTypes.INSTANCE.LSR, OclTypes.INSTANCE.MUTABLE, OclTypes.INSTANCE.METHOD, OclTypes.INSTANCE.MODULE,
                    OclTypes.INSTANCE.MATCH, OclTypes.INSTANCE.MOD, OclTypes.INSTANCE.NONREC, OclTypes.INSTANCE.NEW,
                    OclTypes.INSTANCE.OBJECT, OclTypes.INSTANCE.OPEN, OclTypes.INSTANCE.OF, OclTypes.INSTANCE.OR,
                    OclTypes.INSTANCE.PRIVATE, OclTypes.INSTANCE.REC, OclTypes.INSTANCE.STRUCT, OclTypes.INSTANCE.SIG,
                    OclTypes.INSTANCE.THEN, /*true, */ OclTypes.INSTANCE.TYPE, OclTypes.INSTANCE.TRY, OclTypes.INSTANCE.TO,
                    OclTypes.INSTANCE.VIRTUAL, OclTypes.INSTANCE.VAL, OclTypes.INSTANCE.WHEN,
                    // not reserved
                    OclTypes.INSTANCE.RAISE, OclTypes.INSTANCE.WHILE, OclTypes.INSTANCE.PRI, OclTypes.INSTANCE.WITH,
                    OclTypes.INSTANCE.RECORD, OclTypes.INSTANCE.REF);

    private static final Set<IElementType> OCL_MACRO_TYPES = of(
            OclTypes.INSTANCE.DIRECTIVE_IF, OclTypes.INSTANCE.DIRECTIVE_ELSE,
            OclTypes.INSTANCE.DIRECTIVE_ELIF, OclTypes.INSTANCE.DIRECTIVE_END, OclTypes.INSTANCE.DIRECTIVE_ENDIF);

    private static final Set<IElementType> OCL_OPERATION_SIGN_TYPES =
            of(
                    OclTypes.INSTANCE.L_AND, OclTypes.INSTANCE.L_OR, OclTypes.INSTANCE.SHORTCUT, OclTypes.INSTANCE.ARROW,
                    OclTypes.INSTANCE.PIPE_FORWARD, OclTypes.INSTANCE.EQEQEQ, OclTypes.INSTANCE.EQEQ, OclTypes.INSTANCE.EQ,
                    OclTypes.INSTANCE.NOT_EQEQ, OclTypes.INSTANCE.NOT_EQ, OclTypes.INSTANCE.DIFF, OclTypes.INSTANCE.COLON,
                    OclTypes.INSTANCE.SINGLE_QUOTE, OclTypes.INSTANCE.DOUBLE_QUOTE, OclTypes.INSTANCE.CARRET,
                    OclTypes.INSTANCE.PLUSDOT, OclTypes.INSTANCE.MINUSDOT, OclTypes.INSTANCE.SLASHDOT, OclTypes.INSTANCE.STARDOT,
                    OclTypes.INSTANCE.PLUS, OclTypes.INSTANCE.MINUS, OclTypes.INSTANCE.SLASH, OclTypes.INSTANCE.STAR,
                    OclTypes.INSTANCE.PERCENT, OclTypes.INSTANCE.PIPE, OclTypes.INSTANCE.ARROBASE, OclTypes.INSTANCE.SHARP,
                    OclTypes.INSTANCE.SHARPSHARP, OclTypes.INSTANCE.QUESTION_MARK, OclTypes.INSTANCE.EXCLAMATION_MARK, OclTypes.INSTANCE.LT_OR_EQUAL,
                    OclTypes.INSTANCE.GT_OR_EQUAL, OclTypes.INSTANCE.AMPERSAND, OclTypes.INSTANCE.LEFT_ARROW,
                    OclTypes.INSTANCE.RIGHT_ARROW, OclTypes.INSTANCE.COLON_EQ, OclTypes.INSTANCE.COLON_GT, OclTypes.INSTANCE.STRING_CONCAT,
                    OclTypes.INSTANCE.GT, OclTypes.INSTANCE.GT_BRACE, OclTypes.INSTANCE.GT_BRACKET, OclTypes.INSTANCE.BRACKET_GT,
                    OclTypes.INSTANCE.BRACKET_LT, OclTypes.INSTANCE.BRACE_LT, OclTypes.INSTANCE.DOTDOT);

    private static final Set<IElementType> OCL_OPTIONS_TYPES = of(OclTypes.INSTANCE.NONE, OclTypes.INSTANCE.SOME);

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

    private final ORLangTypes m_types;

    public ORSyntaxHighlighter(ORLangTypes types) {
        m_types = types;
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return m_types instanceof RmlTypes
                ? new RmlLexer()
                : m_types instanceof ResTypes ? new ResLexer() : new OclLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(@NotNull IElementType tokenType) {
        if (tokenType.equals(m_types.MULTI_COMMENT) || tokenType.equals(m_types.SINGLE_COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(m_types.LBRACE) || tokenType.equals(m_types.RBRACE)) {
            return BRACE_KEYS;
        } else if (tokenType.equals(m_types.LBRACKET)
                || tokenType.equals(m_types.RBRACKET)
                || tokenType.equals(m_types.LARRAY)
                || tokenType.equals(m_types.RARRAY)
                || tokenType.equals(m_types.ML_STRING_OPEN)
                || tokenType.equals(m_types.ML_STRING_CLOSE)
                || tokenType.equals(m_types.JS_STRING_OPEN)
                || tokenType.equals(m_types.JS_STRING_CLOSE)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(m_types.LPAREN) || tokenType.equals(m_types.RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(m_types.INT_VALUE) || tokenType.equals(m_types.FLOAT_VALUE)) {
            return NUMBER_KEYS;
        } else if (m_types.DOT.equals(tokenType)) {
            return DOT_KEYS;
        } else if (m_types.TYPE_ARGUMENT.equals(tokenType)) {
            return TYPE_ARGUMENT_KEYS;
        } else if (m_types.POLY_VARIANT.equals(tokenType)) {
            return POLY_VARIANT_KEYS;
        } else if (m_types.COMMA.equals(tokenType)) {
            return COMMA_KEYS;
        } else if (m_types.TAG_AUTO_CLOSE.equals(tokenType) || m_types.TAG_LT_SLASH.equals(tokenType) || m_types.A_LOWER_TAG_NAME.equals(tokenType) || m_types.A_UPPER_TAG_NAME.equals(tokenType)) {
            return MARKUP_TAG_KEYS;
        } else if (m_types.SEMI.equals(tokenType) || m_types.SEMISEMI.equals(tokenType)) {
            return SEMICOLON_KEYS;
        } else if (m_types.STRING_VALUE.equals(tokenType) || m_types.CHAR_VALUE.equals(tokenType)) {
            return STRING_KEYS;
        } else if (m_types == RmlTypes.INSTANCE) {
            if (RML_KEYWORD_TYPES.contains(tokenType)) {
                return KEYWORD_KEYS;
            } else if (RML_OPERATION_SIGN_TYPES.contains(tokenType)) {
                return OPERATION_SIGN_KEYS;
            } else if (RML_OPTIONS_TYPES.contains(tokenType)) {
                return OPTION_KEYS;
            }
        } else if (m_types == ResTypes.INSTANCE) {
            if (RES_KEYWORD_TYPES.contains(tokenType)) {
                return KEYWORD_KEYS;
            } else if (RES_OPERATION_SIGN_TYPES.contains(tokenType)) {
                return OPERATION_SIGN_KEYS;
            } else if (RES_OPTIONS_TYPES.contains(tokenType)) {
                return OPTION_KEYS;
            }
        } else if (m_types == OclTypes.INSTANCE) {
            if (OCL_KEYWORD_TYPES.contains(tokenType)) {
                return KEYWORD_KEYS;
            } else if (OCL_MACRO_TYPES.contains(tokenType)) {
                return MACRO_KEYS;
            } else if (OCL_OPERATION_SIGN_TYPES.contains(tokenType)) {
                return OPERATION_SIGN_KEYS;
            } else if (OCL_OPTIONS_TYPES.contains(tokenType)) {
                return OPTION_KEYS;
            }
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
