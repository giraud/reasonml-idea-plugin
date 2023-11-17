package com.reason.ide.highlight;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public class OclSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    private static final Set<IElementType> KEYWORDS_TYPES =
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

    private static final Set<IElementType> OPERATION_SIGN_TYPES =
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

    private static final Set<IElementType> OPTIONS_TYPES = of(OclTypes.INSTANCE.NONE, OclTypes.INSTANCE.SOME);

    private static final Set<IElementType> MACRO_TYPES = of(
            OclTypes.INSTANCE.DIRECTIVE_IF, OclTypes.INSTANCE.DIRECTIVE_ELSE,
            OclTypes.INSTANCE.DIRECTIVE_ELIF, OclTypes.INSTANCE.DIRECTIVE_END, OclTypes.INSTANCE.DIRECTIVE_ENDIF);

    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new ORSyntaxHighlighter(OclTypes.INSTANCE, KEYWORDS_TYPES, OPERATION_SIGN_TYPES, OPTIONS_TYPES, MACRO_TYPES);
    }
}
