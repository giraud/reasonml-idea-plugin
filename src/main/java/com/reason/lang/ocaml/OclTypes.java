package com.reason.lang.ocaml;

import com.reason.lang.MlTypes;
import com.reason.lang.core.stub.type.*;

public class OclTypes extends MlTypes {

    public static final OclTypes INSTANCE = new OclTypes();

    private OclTypes() {
        ANNOTATION_EXPRESSION = new OclElementType("ANNOTATION_EXPRESSION");
        EXTERNAL_EXPRESSION = new PsiExternalStubElementType("EXTERNAL_EXPRESSION", OclLanguage.INSTANCE);
        EXCEPTION_EXPRESSION = new OclElementType("EXCEPTION_EXPRESSION");
        EXCEPTION_NAME = new OclElementType("EXCEPTION_NAME");
        INCLUDE_EXPRESSION = new OclElementType("INCLUDE_EXPRESSION");
        LET_EXPRESSION = new PsiLetStubElementType("LET_EXPRESSION", OclLanguage.INSTANCE);
        MACRO_EXPRESSION = new OclElementType("MACRO_EXPRESSION");
        MACRO_NAME = new OclElementType("MACRO_NAME");
        MODULE_EXPRESSION = new PsiModuleStubElementType("MODULE_EXPRESSION", OclLanguage.INSTANCE);
        UPPER_SYMBOL = new OclElementType("UPPER_SYMBOL");
        MODULE_PATH = new OclElementType("MODULE_PATH");
        OPEN_EXPRESSION = new OclElementType("OPEN_EXPRESSION");
        TYPE_EXPRESSION = new PsiTypeStubElementType("TYPE_EXPRESSION", OclLanguage.INSTANCE);
        VAL_EXPRESSION = new PsiValStubElementType("VAL_EXPRESSION", OclLanguage.INSTANCE);

        BOOL = new OclElementType("BOOL");
        STRING = new OclElementType("STRING");
        FLOAT = new OclElementType("FLOAT");
        CHAR = new OclElementType("CHAR");
        INT = new OclElementType("INT");

        BOOL_VALUE = new OclElementType("BOOL_VALUE");
        STRING_VALUE = new OclElementType("STRING_VALUE");
        FLOAT_VALUE = new OclElementType("FLOAT_VALUE");
        CHAR_VALUE = new OclElementType("CHAR_VALUE");
        INT_VALUE = new OclElementType("INT_VALUE");

        FUN = new OclElementType("FUN");
        FUNCTION = new OclElementType("FUNCTION");
        FUN_PARAMS = new OclElementType("FUN_PARAMS");
        FUN_BODY = new OclElementType("FUN_BODY");

        FUNCTOR = new OclElementType("FUNCTOR");
        FUNCTOR_PARAMS = new OclElementType("FUNCTOR_PARAMS");

        IF_STATEMENT = new OclElementType("IF");
        LET_BINDING = new OclElementType("LET_BINDING");
        TYPE_CONSTR_NAME = new OclElementType("TYPE_CONSTR_NAME");
        TYPE_BINDING = new OclElementType("TYPE_BINDING");
        PATTERN_MATCH_EXPR = new OclElementType("PATTERN_MATCH_EXPR");
        SCOPED_EXPR = new OclElementType("SCOPED_EXPR");
        LOCAL_OPEN = new OclElementType("LOCAL_OPEN");
        SIG_SCOPE = new OclElementType("SIG_SCOPE");
        PROPERTY_NAME = new OclElementType("PROPERTY_NAME");
        NAMED_SYMBOL = new OclElementType("NAMED_SYMBOL");

        AND = new OclElementType("AND");
        ANDAND = new OclElementType("ANDAND");
        ARROBASE = new OclElementType("ARROBASE");
        ARROW = new OclElementType("ARROW");
        ASSERT = new OclElementType("ASSERT");
        AS = new OclElementType("AS");
        BACKTICK = new OclElementType("BACKTICK");
        BEGIN = new OclElementType("BEGIN");
        CARRET = new OclElementType("CARRET");
        COLON = new OclElementType("COLON");
        COMMA = new OclElementType("COMMA");
        COMMENT = new OclElementType("COMMENT");
        DIFF = new OclElementType("DIFF");
        LT_OR_EQUAL = new OclElementType("LT_OR_EQUAL");
        GT_OR_EQUAL = new OclElementType("GT_OR_EQUAL");
        DOLLAR = new OclElementType("DOLLAR");
        DOT = new OclElementType("DOT");
        DOTDOTDOT = new OclElementType("DOTDOTDOT");
        DO = new OclElementType("DO");
        DONE = new OclElementType("DONE");
        ELSE = new OclElementType("ELSE");
        END = new OclElementType("END");
        NOT_EQ = new OclElementType("EQ");
        NOT_EQEQ = new OclElementType("EQEQ");
        EQ = new OclElementType("EQ");
        EQEQ = new OclElementType("EQEQ");
        EQEQEQ = new OclElementType("EQEQEQ");
        EXCEPTION = new OclElementType("EXCEPTION");
        EXCLAMATION_MARK = new OclElementType("EXCLAMATION_MARK");
        EXTERNAL = new OclElementType("EXTERNAL");
        FALSE = new OclElementType("FALSE");
        FOR = new OclElementType("FOR");
        TYPE_ARGUMENT = new OclElementType("TYPE_ARGUMENT");
        GT = new OclElementType("GT");
        BIN_CONDITION = new OclElementType("BIN_CONDITION");
        IN = new OclElementType("IN");
        LAZY = new OclElementType("LAZY");
        INCLUDE = new OclElementType("INCLUDE");
        LARRAY = new OclElementType("LARRAY");
        LBRACE = new OclElementType("LBRACE");
        LBRACKET = new OclElementType("LBRACKET");
        LET = new OclElementType("LET");
        LIDENT = new OclElementType("LIDENT");
        LIST = new OclElementType("LIST");
        LPAREN = new OclElementType("LPAREN");
        LT = new OclElementType("LT");
        MATCH = new OclElementType("MATCH");
        MINUS = new OclElementType("MINUS");
        MINUSDOT = new OclElementType("MINUSDOT");
        MODULE = new OclElementType("MODULE");
        MUTABLE = new OclElementType("MUTABLE");
        NONE = new OclElementType("NONE");
        OF = new OclElementType("OF");
        OPEN = new OclElementType("OPEN");
        OPTION = new OclElementType("OPTION");
        POLY_VARIANT = new OclElementType("POLY_VARIANT");
        VARIANT = new OclElementType("VARIANT");
        VARIANT_NAME = new OclElementType("VARIANT_NAME");
        PIPE = new OclElementType("PIPE");
        PIPE_FORWARD = new OclElementType("PIPE_FORWARD");
        PIPE_FIRST = new OclElementType("PIPE_FIRST");
        PLUS = new OclElementType("PLUS");
        PERCENT = new OclElementType("PERCENT");
        PLUSDOT = new OclElementType("PLUSDOT");
        QUESTION_MARK = new OclElementType("QUESTION_MARK");
        QUOTE = new OclElementType("QUOTE");
        RAISE = new OclElementType("RAISE");
        RARRAY = new OclElementType("RARRAY");
        RBRACE = new OclElementType("RBRACE");
        RBRACKET = new OclElementType("RBRACKET");
        REC = new OclElementType("REC");
        REF = new OclElementType("REF");
        RPAREN = new OclElementType("RPAREN");
        SEMI = new OclElementType("SEMI");
        SIG = new OclElementType("SIG");
        SHARP = new OclElementType("SHARP");
        SHARPSHARP = new OclElementType("SHARPSHARP");
        SHORTCUT = new OclElementType("SHORTCUT");
        SLASH = new OclElementType("SLASH");
        SLASHDOT = new OclElementType("SLASHDOT");
        SOME = new OclElementType("SOME");
        STAR = new OclElementType("STAR");
        STARDOT = new OclElementType("STARDOT");
        STRUCT = new OclElementType("STRUCT");
        SWITCH = new OclElementType("SWITCH");
        TAG_AUTO_CLOSE = new OclElementType("TAG_AUTO_CLOSE");
        TAG_START = new OclElementType("TAG_START");
        TAG_CLOSE = new OclElementType("TAG_CLOSE");
        TAG_NAME = new OclElementType("TAG_NAME");
        TAG_PROPERTY = new OclElementType("TAG_PROPERTY");
        TAG_LT = new OclElementType("TAG_LT");
        TAG_LT_SLASH = new OclElementType("TAG_LT_SLASH");
        TAG_GT = new OclElementType("TAG_GT");
        TILDE = new OclElementType("TILDE");
        TO = new OclElementType("TO");
        THEN = new OclElementType("THEN");
        TRUE = new OclElementType("TRUE");
        TRY = new OclElementType("TRY");
        TYPE = new OclElementType("TYPE");
        UIDENT = new OclElementType("UIDENT");
        UNIT = new OclElementType("UNIT");
        VAL = new OclElementType("VAL");
        PUB = new OclElementType("PUB");
        LOWER_SYMBOL = new OclElementType("LOWER_SYMBOL");
        WHEN = new OclElementType("WHEN");
        WHILE = new OclElementType("WHILE");
        WITH = new OclElementType("WITH");

        ASR = new OclElementType("ASR");
        CLASS = new OclElementType("CLASS");
        CONSTRAINT = new OclElementType("CONSTRAINT");
        DOWNTO = new OclElementType("DOWNTO");
        INHERIT = new OclElementType("INHERIT");
        INITIALIZER = new OclElementType("INITIALIZER");
        LAND = new OclElementType("LAND");
        LOR = new OclElementType("LOR");
        LSL = new OclElementType("LSL");
        LSR = new OclElementType("LSR");
        LXOR = new OclElementType("LXOR");
        METHOD = new OclElementType("METHOD");
        MOD = new OclElementType("MOD");
        NEW = new OclElementType("NEW");
        NONREC = new OclElementType("NONREC");
        OR = new OclElementType("OR");
        PRIVATE = new OclElementType("PRIVATE");
        VIRTUAL = new OclElementType("VIRTUAL");

        COLON_EQ = new OclElementType("COLON_EQ");
        COLON_GT = new OclElementType("COLON_GT");
        DOTDOT = new OclElementType("DOTDOT");
        SEMISEMI = new OclElementType("SEMISEMI");
        GT_BRACKET = new OclElementType("GT_BRACKET");
        GT_BRACE = new OclElementType("GT_BRACE");
        LEFT_ARROW = new OclElementType("LEFT_ARROW");
        RIGHT_ARROW = new OclElementType("RIGHT_ARROW");

        OBJECT = new OclElementType("OBJECT");
        RECORD = new OclElementType("RECORD");
        RECORD_FIELD = new OclElementType("RECORD_FIELD");

        AMPERSAND = new OclElementType("AMPERSAND");
        BRACKET_GT = new OclElementType("BRACKET_GT");
        BRACKET_LT = new OclElementType("BRACKET_LT");
        BRACE_LT = new OclElementType("BRACE_LT");

        ML_STRING_OPEN = new OclElementType("ML_STRING_OPEN");
        ML_STRING_CLOSE = new OclElementType("ML_STRING_CLOSE");
        JS_STRING_OPEN = new OclElementType("JS_STRING_OPEN");
        JS_STRING_CLOSE = new OclElementType("JS_STRING_CLOSE");
        INTERPOLATION = new OclElementType("INTERPOLATION");
    }

}
