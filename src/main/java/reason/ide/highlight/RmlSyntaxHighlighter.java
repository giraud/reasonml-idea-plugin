package reason.ide.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import reason.lang.RmlLexerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static reason.lang.RmlTypes.*;

public class RmlSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Set<IElementType> KEYWORD_TYPES = of(
            OPEN, MODULE, FUN, LET, TYPE, INCLUDE, EXTERNAL, IF, ELSE, SWITCH, TRY, RAISE, FOR, IN, TO, TRUE, FALSE,
            REF,
            // OCaml
            MATCH, WITH, DO, DONE, OBJECT, END, LAZY, ASSERT, THEN, FUNCTION, STRUCT, SIG, VAL
    );
    private static final Set<IElementType> OPERATION_SIGN_TYPES = of(
            ANDAND, SHORTCUT, ARROW, SIMPLE_ARROW, PIPE_FORWARD,
            EQEQEQ, EQEQ, EQ, NOT_EQEQ, NOT_EQ, DIFF, COLON, QUOTE,
            CARRET, PLUSDOT, MINUSDOT, SLASHDOT, STARDOT, PLUS, MINUS, SLASH, STAR, PERCENT,
            PIPE, ARROBASE, SHARP, QUESTION_MARK, EXCLAMATION_MARK
    );
    private static final Set<IElementType> OPTIONS_TYPES = of(NONE, SOME, OPTION);

    private static final TextAttributesKey TYPE_ARGUMENT_KEY = TextAttributesKey.createTextAttributesKey("TYPE_ARGUMENT");
    private static final TextAttributesKey POLY_VARIANT_KEY = TextAttributesKey.createTextAttributesKey("POLY_VARIANT");

    public static final TextAttributesKey RML_COMMENT_ = createTextAttributesKey("REASONML_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey STRING_ = createTextAttributesKey("REASONML_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER_ = createTextAttributesKey("REASONML_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey TAG_ = createTextAttributesKey("REASONML_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey MODULE_NAME_ = createTextAttributesKey("REASONML_UIDENT", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey OPTION_ = createTextAttributesKey("REASONML_OPTION");
    public static final TextAttributesKey KEYWORD_ = createTextAttributesKey("REASONML_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SEMICOLON_ = createTextAttributesKey("REASONML_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey BRACKETS_ = createTextAttributesKey("REASONML_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey BRACES_ = createTextAttributesKey("REASONML_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey PARENS_ = createTextAttributesKey("REASONML_PARENS", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey OPERATION_SIGN_ = createTextAttributesKey("REASONML_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey TYPE_ARGUMENT_ = createTextAttributesKey("REASONML_TYPE_ARGUMENT", TYPE_ARGUMENT_KEY);
    public static final TextAttributesKey POLY_VARIANT_ = createTextAttributesKey("REASONML_POLY_VARIANT", POLY_VARIANT_KEY);
    private static final TextAttributesKey DOT_ = createTextAttributesKey("REASONML_OPERATION_SIGN", DefaultLanguageHighlighterColors.DOT);
    private static final TextAttributesKey COMMA_ = createTextAttributesKey("REASONML_OPERATION_SIGN", DefaultLanguageHighlighterColors.COMMA);
    private static final TextAttributesKey BAD_CHAR_ = createTextAttributesKey("REASONML_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER_};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{RML_COMMENT_};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING_};
    private static final TextAttributesKey[] TYPE_ARGUMENT_KEYS = new TextAttributesKey[]{TYPE_ARGUMENT_};
    private static final TextAttributesKey[] POLY_VARIANT_KEYS = new TextAttributesKey[]{POLY_VARIANT_};
    private static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKETS_};
    private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACES_};
    private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PARENS_};
    private static final TextAttributesKey[] OPTION_KEYS = new TextAttributesKey[]{OPTION_};
    private static final TextAttributesKey[] TAG_KEYS = new TextAttributesKey[]{TAG_};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD_};
    private static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON_};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{DOT_};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA_};
    private static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{OPERATION_SIGN_};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR_};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new RmlLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(LBRACE) || tokenType.equals(RBRACE)) {
            return BRACE_KEYS;
        } else if (tokenType.equals(LBRACKET) || tokenType.equals(RBRACKET) || tokenType.equals(LARRAY) || tokenType.equals(RARRAY)) {
            return BRACKET_KEYS;
        } else if (tokenType.equals(LPAREN) || tokenType.equals(RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(INT) || tokenType.equals(FLOAT)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(AUTO_CLOSE_TAG) || tokenType.equals(CLOSE_TAG) || tokenType.equals(GT) || tokenType.equals(LT)) {
            return TAG_KEYS;
        } else if (DOT.equals(tokenType)) {
            return DOT_KEYS;
        } else if (TYPE_ARGUMENT.equals(tokenType)) {
            return TYPE_ARGUMENT_KEYS;
        } else if (POLY_VARIANT.equals(tokenType)) {
            return POLY_VARIANT_KEYS;
        } else if (COMMA.equals(tokenType)) {
            return COMMA_KEYS;
        } else if (SEMI.equals(tokenType)) {
            return SEMICOLON_KEYS;
        } else if (STRING.equals(tokenType)) {
            return STRING_KEYS;
        } else if (KEYWORD_TYPES.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else if (OPERATION_SIGN_TYPES.contains(tokenType)) {
            return OPERATION_SIGN_KEYS;
        } else if (OPTIONS_TYPES.contains(tokenType)) {
            return OPTION_KEYS;
        } else if (BAD_CHARACTER.equals(tokenType)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

    private static Set<IElementType> of(IElementType... types) {
        Set<IElementType> result = new HashSet<>();
        Collections.addAll(result, types);
        return result;
    }
}
