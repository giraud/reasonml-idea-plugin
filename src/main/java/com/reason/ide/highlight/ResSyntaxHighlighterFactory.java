package com.reason.ide.highlight;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public class ResSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    private static final Set<IElementType> KEYWORD_TYPES =
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
                    ResTypes.INSTANCE.DIRECTIVE_ENDIF, ResTypes.INSTANCE.UNPACK,
                    //
                    ResTypes.INSTANCE.ASYNC, ResTypes.INSTANCE.AWAIT);

    private static final Set<IElementType> OPERATION_SIGN_TYPES =
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

    private static final Set<IElementType> OPTION_TYPES =
            of(ResTypes.INSTANCE.NONE, ResTypes.INSTANCE.SOME);

    private static final Set<IElementType> MACRO_TYPES = Collections.emptySet();

    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new ORSyntaxHighlighter(ResTypes.INSTANCE, KEYWORD_TYPES, OPERATION_SIGN_TYPES, OPTION_TYPES, MACRO_TYPES);
    }
}
