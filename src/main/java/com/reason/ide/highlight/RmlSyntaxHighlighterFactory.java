package com.reason.ide.highlight;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public class RmlSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    public static final Set<IElementType> KEYWORD_TYPES =
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

    public static final Set<IElementType> OPERATION_SIGN_TYPES =
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

    public static final Set<IElementType> OPTION_TYPES =
            of(RmlTypes.INSTANCE.NONE, RmlTypes.INSTANCE.SOME);

    public static final Set<IElementType> MACRO_TYPES = Collections.emptySet();

    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new ORSyntaxHighlighter(RmlTypes.INSTANCE, KEYWORD_TYPES, OPERATION_SIGN_TYPES, OPTION_TYPES, MACRO_TYPES);
    }
}
