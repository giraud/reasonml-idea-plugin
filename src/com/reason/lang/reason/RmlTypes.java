package com.reason.lang.reason;

import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;

public class RmlTypes extends ORTypes {

    public static final RmlTypes INSTANCE = new RmlTypes();

    private RmlTypes() {
        // Stub element types

        C_FAKE_MODULE = (ORCompositeType) RmlStubBasedElementTypes.C_FAKE_MODULE;
        C_EXCEPTION_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_EXCEPTION_DECLARATION;
        C_TYPE_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_TYPE_DECLARATION;
        C_EXTERNAL_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_EXTERNAL_DECLARATION;
        C_LET_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_LET_DECLARATION;
        C_MODULE_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_MODULE_DECLARATION;
        C_VAL_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_VAL_DECLARATION;
        C_FUN_PARAM = (ORCompositeType) RmlStubBasedElementTypes.C_FUN_PARAM;
        C_FUNCTOR_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_FUNCTOR_DECLARATION;
        C_FUNCTOR_PARAM = (ORCompositeType) RmlStubBasedElementTypes.C_FUNCTOR_PARAM;
        C_RECORD_FIELD = (ORCompositeType) RmlStubBasedElementTypes.C_RECORD_FIELD;
        C_VARIANT_DECLARATION = (ORCompositeType) RmlStubBasedElementTypes.C_VARIANT_DECLARATION;
        C_INCLUDE = (ORCompositeType) RmlStubBasedElementTypes.C_INCLUDE;
        C_OPEN = (ORCompositeType) RmlStubBasedElementTypes.C_OPEN;

        // Composite element types

        C_ANNOTATION = new ORCompositeElementType("C_ANNOTATION", RmlLanguage.INSTANCE);
        C_MIXIN_FIELD = new ORCompositeElementType("C_MIXIN_FIELD", RmlLanguage.INSTANCE);
        C_ASSERT_STMT = new ORCompositeElementType("C_ASSERT_STMT", RmlLanguage.INSTANCE);
        C_BINARY_CONDITION = new ORCompositeElementType("C_BIN_CONDITION", RmlLanguage.INSTANCE);
        C_CLASS_DECLARATION = new ORCompositeElementType("C_CLASS_DECLARATION", RmlLanguage.INSTANCE);
        C_CLASS_CONSTR = new ORCompositeElementType("C_CLASS_CONSTR", RmlLanguage.INSTANCE);
        C_CLASS_PARAMS = new ORCompositeElementType("C_CLASS_PARAMS", RmlLanguage.INSTANCE);
        C_CLASS_FIELD = new ORCompositeElementType("C_CLASS_FIELD", RmlLanguage.INSTANCE);
        C_CLASS_METHOD = new ORCompositeElementType("C_CLASS_METHOD", RmlLanguage.INSTANCE);
        C_CONSTRAINTS = new ORCompositeElementType("C_CONSTRAINTS", RmlLanguage.INSTANCE);
        C_CONSTRAINT = new ORCompositeElementType("C_CONSTRAINT", RmlLanguage.INSTANCE);
        C_CUSTOM_OPERATOR = new ORCompositeElementType("C_CUSTOM_OPERATOR", RmlLanguage.INSTANCE);
        C_DECONSTRUCTION = new ORCompositeElementType("C_DECONSTRUCTION", RmlLanguage.INSTANCE);
        C_DEFAULT_VALUE = new ORCompositeElementType("C_DEFAULT_VALUE", RmlLanguage.INSTANCE);
        C_DIRECTIVE = new ORCompositeElementType("C_DIRECTIVE", RmlLanguage.INSTANCE);
        C_DO_LOOP = new ORCompositeElementType("C_DO_LOOP", RmlLanguage.INSTANCE);
        C_LOWER_IDENTIFIER = new ORCompositeElementType("C_LOWER_IDENTIFIER", RmlLanguage.INSTANCE);
        C_UPPER_IDENTIFIER = new ORCompositeElementType("C_UPPER_IDENTIFIER", RmlLanguage.INSTANCE);
        C_FUN_CALL_PARAMS = new ORCompositeElementType("C_FUN_CALL_PARAMS", RmlLanguage.INSTANCE);
        C_FUN_EXPR = new ORCompositeElementType("C_FUN_EXPR", RmlLanguage.INSTANCE);
        C_FUN_PARAMS = new ORCompositeElementType("C_FUN_PARAMS", RmlLanguage.INSTANCE);
        C_FUN_BODY = new ORCompositeElementType("C_FUN_BODY", RmlLanguage.INSTANCE);
        C_FUNCTOR_BINDING = new ORCompositeElementType("C_FUNCTOR_BINDING", RmlLanguage.INSTANCE);
        C_FUNCTOR_CALL = new ORCompositeElementType("C_FUNCTOR_CALL", RmlLanguage.INSTANCE);
        C_FUNCTOR_PARAMS = new ORCompositeElementType("C_FUNCTOR_PARAMS", RmlLanguage.INSTANCE);
        C_FUNCTOR_RESULT = new ORCompositeElementType("C_FUNCTOR_RESULT", RmlLanguage.INSTANCE);
        C_IF = new ORCompositeElementType("C_IF", RmlLanguage.INSTANCE);
        C_IF_THEN_SCOPE = new ORCompositeElementType("C_IF_THEN_SCOPE", RmlLanguage.INSTANCE);
        C_INTERPOLATION_EXPR = new ORCompositeElementType("C_INTERPOLATION_EXPR", RmlLanguage.INSTANCE);
        C_INTERPOLATION_PART = new ORCompositeElementType("C_INTERPOLATION_PART", RmlLanguage.INSTANCE);
        C_INTERPOLATION_REF = new ORCompositeElementType("C_INTERPOLATION_REF", RmlLanguage.INSTANCE);
        C_JS_OBJECT = new ORCompositeElementType("C_JS_OBJECT", RmlLanguage.INSTANCE);
        C_LET_ATTR = new ORCompositeElementType("C_LET_ATTR", RmlLanguage.INSTANCE);
        C_LET_BINDING = new ORCompositeElementType("C_LET_BINDING", RmlLanguage.INSTANCE);
        C_LOCAL_OPEN = new ORCompositeElementType("C_LOCAL_OPEN", RmlLanguage.INSTANCE);
        C_TYPE_VARIABLE = new ORCompositeElementType("C_TYPE_VARIABLE", RmlLanguage.INSTANCE);
        C_LOWER_SYMBOL = new ORCompositeElementType("C_LOWER_SYMBOL", RmlLanguage.INSTANCE);
        C_MACRO_EXPR = new ORCompositeElementType("C_MACRO_EXPR", RmlLanguage.INSTANCE);
        C_MACRO_NAME = new ORCompositeElementType("C_MACRO_NAME", RmlLanguage.INSTANCE);
        C_NAMED_PARAM = new ORCompositeElementType("C_NAMED_PARAM", RmlLanguage.INSTANCE);
        C_MACRO_RAW_BODY = new ORCompositeElementType("C_MACRO_RAW_BODY", RmlLanguage.INSTANCE);
        C_MODULE_TYPE = new ORCompositeElementType("C_MODULE_TYPE", RmlLanguage.INSTANCE);
        C_ML_INTERPOLATOR = new ORCompositeElementType("C_ML_INTERPOLATOR", RmlLanguage.INSTANCE);
        C_OBJECT = new ORCompositeElementType("C_OBJECT", RmlLanguage.INSTANCE);
        C_OBJECT_FIELD = new ORCompositeElementType("C_OBJECT_FIELD", RmlLanguage.INSTANCE);
        C_OPTION = new ORCompositeElementType("C_OPTION", RmlLanguage.INSTANCE);
        C_PARAMETERS = new ORCompositeElementType("C_PARAMETERS", RmlLanguage.INSTANCE);
        C_PATTERN_MATCH_BODY = new ORCompositeElementType("C_PATTERN_MATCH_BODY", RmlLanguage.INSTANCE);
        C_PATTERN_MATCH_EXPR = new ORCompositeElementType("C_PATTERN_MATCH_EXPR", RmlLanguage.INSTANCE);
        C_RECORD_EXPR = new ORCompositeElementType("C_RECORD_EXPR", RmlLanguage.INSTANCE);
        C_RAW = new ORCompositeElementType("C_RAW", RmlLanguage.INSTANCE);
        C_SIG_EXPR = new ORCompositeElementType("C_SIG_EXPR", RmlLanguage.INSTANCE);
        C_SIG_ITEM = new ORCompositeElementType("C_SIG_ITEM", RmlLanguage.INSTANCE);
        C_SCOPED_EXPR = new ORCompositeElementType("C_SCOPED_EXPR", RmlLanguage.INSTANCE);
        C_STRUCT_EXPR = new ORCompositeElementType("C_STRUCT_EXPR", RmlLanguage.INSTANCE);
        C_SWITCH_EXPR = new ORCompositeElementType("C_SWITCH_EXPR", RmlLanguage.INSTANCE);
        C_TAG = new ORCompositeElementType("C_TAG", RmlLanguage.INSTANCE);
        C_TAG_PROP_VALUE = new ORCompositeElementType("C_TAG_PROP_VALUE", RmlLanguage.INSTANCE);
        C_TAG_BODY = new ORCompositeElementType("C_TAG_BODY", RmlLanguage.INSTANCE);
        C_TAG_CLOSE = new ORCompositeElementType("C_TAG_CLOSE", RmlLanguage.INSTANCE);
        C_TAG_PROPERTY = new ORCompositeElementType("C_TAG_PROPERTY", RmlLanguage.INSTANCE);
        C_TAG_START = new ORCompositeElementType("C_TAG_START", RmlLanguage.INSTANCE);
        C_TERNARY = new ORCompositeElementType("C_TERNARY", RmlLanguage.INSTANCE);
        C_TRY_EXPR = new ORCompositeElementType("C_TRY_EXPR", RmlLanguage.INSTANCE);
        C_TRY_BODY = new ORCompositeElementType("C_TRY_BODY", RmlLanguage.INSTANCE);
        C_TRY_HANDLER = new ORCompositeElementType("C_TRY_HANDLER", RmlLanguage.INSTANCE);
        C_TRY_HANDLERS = new ORCompositeElementType("C_TRY_HANDLERS", RmlLanguage.INSTANCE);
        C_TYPE_BINDING = new ORCompositeElementType("C_TYPE_BINDING", RmlLanguage.INSTANCE);
        C_UNIT = new ORCompositeElementType("C_UNIT", RmlLanguage.INSTANCE);
        C_DUMMY = new ORCompositeElementType("C_DUMMY", RmlLanguage.INSTANCE);
        C_UPPER_SYMBOL = new ORCompositeElementType("C_UPPER_SYMBOL", RmlLanguage.INSTANCE);
        C_VARIANT = new ORCompositeElementType("C_VARIANT", RmlLanguage.INSTANCE);
        C_VARIANT_CONSTRUCTOR =
                new ORCompositeElementType("C_VARIANT_CONSTRUCTOR", RmlLanguage.INSTANCE);
        C_WHILE = new ORCompositeElementType("C_WHILE", RmlLanguage.INSTANCE);

        // Token element types

        BOOL_VALUE = new ORTokenElementType("BOOL_VALUE", RmlLanguage.INSTANCE);
        STRING_VALUE = new ORTokenElementType("STRING_VALUE", RmlLanguage.INSTANCE);
        FLOAT_VALUE = new ORTokenElementType("FLOAT_VALUE", RmlLanguage.INSTANCE);
        CATCH = new ORTokenElementType("CATCH", RmlLanguage.INSTANCE);
        CHAR_VALUE = new ORTokenElementType("CHAR_VALUE", RmlLanguage.INSTANCE);
        INT_VALUE = new ORTokenElementType("INT_VALUE", RmlLanguage.INSTANCE);
        EXCEPTION_NAME = new ORTokenElementType("EXCEPTION_NAME", RmlLanguage.INSTANCE);
        PROPERTY_NAME = new ORTokenElementType("PROPERTY_NAME", RmlLanguage.INSTANCE);
        SWITCH = new ORTokenElementType("SWITCH", RmlLanguage.INSTANCE);
        FUNCTION = new ORTokenElementType("FUNCTION", RmlLanguage.INSTANCE);
        FUN = new ORTokenElementType("FUN", RmlLanguage.INSTANCE);
        FUNCTOR = new ORTokenElementType("FUNCTOR", RmlLanguage.INSTANCE);
        IF = new ORTokenElementType("IF", RmlLanguage.INSTANCE);
        AND = new ORTokenElementType("AND", RmlLanguage.INSTANCE);
        L_AND = new ORTokenElementType("L_AND", RmlLanguage.INSTANCE);
        L_OR = new ORTokenElementType("L_OR", RmlLanguage.INSTANCE);
        ARROBASE = new ORTokenElementType("ARROBASE", RmlLanguage.INSTANCE);
        ARROBASE_2 = new ORTokenElementType("ARROBASE_2", RmlLanguage.INSTANCE);
        ARROBASE_3 = new ORTokenElementType("ARROBASE_3", RmlLanguage.INSTANCE);
        ARROW = new ORTokenElementType("ARROW", RmlLanguage.INSTANCE);
        ASSERT = new ORTokenElementType("ASSERT", RmlLanguage.INSTANCE);
        AS = new ORTokenElementType("AS", RmlLanguage.INSTANCE);
        BACKTICK = new ORTokenElementType("BACKTICK", RmlLanguage.INSTANCE);
        BEGIN = new ORTokenElementType("BEGIN", RmlLanguage.INSTANCE);
        CARRET = new ORTokenElementType("CARRET", RmlLanguage.INSTANCE);
        COLON = new ORTokenElementType("COLON", RmlLanguage.INSTANCE);
        COMMA = new ORTokenElementType("COMMA", RmlLanguage.INSTANCE);
        SINGLE_COMMENT = new ORTokenElementType("SINGLE_COMMENT", RmlLanguage.INSTANCE);
        MULTI_COMMENT = new ORTokenElementType("MULTI_COMMENT", RmlLanguage.INSTANCE);
        DIFF = new ORTokenElementType("DIFF", RmlLanguage.INSTANCE);
        DIRECTIVE_IF = new ORTokenElementType("DIRECTIVE_IF", RmlLanguage.INSTANCE);
        DIRECTIVE_ELSE = new ORTokenElementType("DIRECTIVE_ELSE", RmlLanguage.INSTANCE);
        DIRECTIVE_ELIF = new ORTokenElementType("DIRECTIVE_ELIF", RmlLanguage.INSTANCE);
        DIRECTIVE_END = new ORTokenElementType("DIRECTIVE_END", RmlLanguage.INSTANCE);
        DIRECTIVE_ENDIF = new ORTokenElementType("DIRECTIVE_ENDIF", RmlLanguage.INSTANCE);
        LT_OR_EQUAL = new ORTokenElementType("LT_OR_EQUAL", RmlLanguage.INSTANCE);
        GT_OR_EQUAL = new ORTokenElementType("GT_OR_EQUAL", RmlLanguage.INSTANCE);
        DOLLAR = new ORTokenElementType("DOLLAR", RmlLanguage.INSTANCE);
        DOT = new ORTokenElementType("DOT", RmlLanguage.INSTANCE);
        DOTDOTDOT = new ORTokenElementType("DOTDOTDOT", RmlLanguage.INSTANCE);
        DO = new ORTokenElementType("DO", RmlLanguage.INSTANCE);
        DONE = new ORTokenElementType("DONE", RmlLanguage.INSTANCE);
        ELSE = new ORTokenElementType("ELSE", RmlLanguage.INSTANCE);
        END = new ORTokenElementType("END", RmlLanguage.INSTANCE);
        ENDIF = new ORTokenElementType("ENDIF", RmlLanguage.INSTANCE);
        NOT_EQ = new ORTokenElementType("EQ", RmlLanguage.INSTANCE);
        NOT_EQEQ = new ORTokenElementType("EQEQ", RmlLanguage.INSTANCE);
        EQ = new ORTokenElementType("EQ", RmlLanguage.INSTANCE);
        EQEQ = new ORTokenElementType("EQEQ", RmlLanguage.INSTANCE);
        EQEQEQ = new ORTokenElementType("EQEQEQ", RmlLanguage.INSTANCE);
        EXCEPTION = new ORTokenElementType("EXCEPTION", RmlLanguage.INSTANCE);
        EXCLAMATION_MARK = new ORTokenElementType("EXCLAMATION_MARK", RmlLanguage.INSTANCE);
        EXTERNAL = new ORTokenElementType("EXTERNAL", RmlLanguage.INSTANCE);
        FOR = new ORTokenElementType("FOR", RmlLanguage.INSTANCE);
        TYPE_ARGUMENT = new ORTokenElementType("TYPE_ARGUMENT", RmlLanguage.INSTANCE);
        GT = new ORTokenElementType("GT", RmlLanguage.INSTANCE);
        IN = new ORTokenElementType("IN", RmlLanguage.INSTANCE);
        LAZY = new ORTokenElementType("LAZY", RmlLanguage.INSTANCE);
        INCLUDE = new ORTokenElementType("INCLUDE", RmlLanguage.INSTANCE);
        LARRAY = new ORTokenElementType("LARRAY", RmlLanguage.INSTANCE);
        LBRACE = new ORTokenElementType("LBRACE", RmlLanguage.INSTANCE);
        LBRACKET = new ORTokenElementType("LBRACKET", RmlLanguage.INSTANCE);
        LET = new ORTokenElementType("LET", RmlLanguage.INSTANCE);
        LIDENT = new ORTokenElementType("LIDENT", RmlLanguage.INSTANCE);
        LPAREN = new ORTokenElementType("LPAREN", RmlLanguage.INSTANCE);
        LT = new ORTokenElementType("LT", RmlLanguage.INSTANCE);
        MATCH = new ORTokenElementType("MATCH", RmlLanguage.INSTANCE);
        MINUS = new ORTokenElementType("MINUS", RmlLanguage.INSTANCE);
        MINUSDOT = new ORTokenElementType("MINUSDOT", RmlLanguage.INSTANCE);
        MODULE = new ORTokenElementType("MODULE", RmlLanguage.INSTANCE);
        MUTABLE = new ORTokenElementType("MUTABLE", RmlLanguage.INSTANCE);
        NONE = new ORTokenElementType("NONE", RmlLanguage.INSTANCE);
        OF = new ORTokenElementType("OF", RmlLanguage.INSTANCE);
        OPEN = new ORTokenElementType("OPEN", RmlLanguage.INSTANCE);
        OPTION = new ORTokenElementType("OPTION", RmlLanguage.INSTANCE);
        POLY_VARIANT = new ORTokenElementType("POLY_VARIANT", RmlLanguage.INSTANCE);
        VARIANT_NAME = new ORTokenElementType("VARIANT_NAME", RmlLanguage.INSTANCE);
        PIPE = new ORTokenElementType("PIPE", RmlLanguage.INSTANCE);
        PIPE_FORWARD = new ORTokenElementType("PIPE_FORWARD", RmlLanguage.INSTANCE);
        PLUS = new ORTokenElementType("PLUS", RmlLanguage.INSTANCE);
        PERCENT = new ORTokenElementType("PERCENT", RmlLanguage.INSTANCE);
        PLUSDOT = new ORTokenElementType("PLUSDOT", RmlLanguage.INSTANCE);
        QUESTION_MARK = new ORTokenElementType("QUESTION_MARK", RmlLanguage.INSTANCE);
        SINGLE_QUOTE = new ORTokenElementType("SINGLE_QUOTE", RmlLanguage.INSTANCE);
        DOUBLE_QUOTE = new ORTokenElementType("DOUBLE_QUOTE", RmlLanguage.INSTANCE);
        RAISE = new ORTokenElementType("RAISE", RmlLanguage.INSTANCE);
        RARRAY = new ORTokenElementType("RARRAY", RmlLanguage.INSTANCE);
        RBRACE = new ORTokenElementType("RBRACE", RmlLanguage.INSTANCE);
        RBRACKET = new ORTokenElementType("RBRACKET", RmlLanguage.INSTANCE);
        REC = new ORTokenElementType("REC", RmlLanguage.INSTANCE);
        REF = new ORTokenElementType("REF", RmlLanguage.INSTANCE);
        RPAREN = new ORTokenElementType("RPAREN", RmlLanguage.INSTANCE);
        SEMI = new ORTokenElementType("SEMI", RmlLanguage.INSTANCE);
        SIG = new ORTokenElementType("SIG", RmlLanguage.INSTANCE);
        SHARP = new ORTokenElementType("SHARP", RmlLanguage.INSTANCE);
        SHARPSHARP = new ORTokenElementType("SHARPSHARP", RmlLanguage.INSTANCE);
        SHORTCUT = new ORTokenElementType("SHORTCUT", RmlLanguage.INSTANCE);
        SLASH = new ORTokenElementType("SLASH", RmlLanguage.INSTANCE);
        SLASH_2 = new ORTokenElementType("SLASH_2", RmlLanguage.INSTANCE);
        SLASHDOT = new ORTokenElementType("SLASHDOT", RmlLanguage.INSTANCE);
        SOME = new ORTokenElementType("SOME", RmlLanguage.INSTANCE);
        STAR = new ORTokenElementType("STAR", RmlLanguage.INSTANCE);
        STARDOT = new ORTokenElementType("STARDOT", RmlLanguage.INSTANCE);
        STRUCT = new ORTokenElementType("STRUCT", RmlLanguage.INSTANCE);
        OP_STRUCT_DIFF = new ORTokenElementType("OP_STRUCT_DIFF", RmlLanguage.INSTANCE);
        TAG_AUTO_CLOSE = new ORTokenElementType("TAG_AUTO_CLOSE", RmlLanguage.INSTANCE);
        TAG_NAME = new ORTokenElementType("TAG_NAME", RmlLanguage.INSTANCE);
        TAG_LT = new ORTokenElementType("TAG_LT", RmlLanguage.INSTANCE);
        TAG_LT_SLASH = new ORTokenElementType("TAG_LT_SLASH", RmlLanguage.INSTANCE);
        TAG_GT = new ORTokenElementType("TAG_GT", RmlLanguage.INSTANCE);
        TILDE = new ORTokenElementType("TILDE", RmlLanguage.INSTANCE);
        TO = new ORTokenElementType("TO", RmlLanguage.INSTANCE);
        THEN = new ORTokenElementType("THEN", RmlLanguage.INSTANCE);
        TRY = new ORTokenElementType("TRY", RmlLanguage.INSTANCE);
        TYPE = new ORTokenElementType("TYPE", RmlLanguage.INSTANCE);
        UIDENT = new ORTokenElementType("UIDENT", RmlLanguage.INSTANCE);
        UNIT = new ORTokenElementType("UNIT", RmlLanguage.INSTANCE);
        VAL = new ORTokenElementType("VAL", RmlLanguage.INSTANCE);
        PUB = new ORTokenElementType("PUB", RmlLanguage.INSTANCE);
        PRI = new ORTokenElementType("PRI", RmlLanguage.INSTANCE);
        WHEN = new ORTokenElementType("WHEN", RmlLanguage.INSTANCE);
        WHILE = new ORTokenElementType("WHILE", RmlLanguage.INSTANCE);
        WITH = new ORTokenElementType("WITH", RmlLanguage.INSTANCE);
        RAW = new ORTokenElementType("RAW", RmlLanguage.INSTANCE);

        ASR = new ORTokenElementType("ASR", RmlLanguage.INSTANCE);
        CLASS = new ORTokenElementType("CLASS", RmlLanguage.INSTANCE);
        CONSTRAINT = new ORTokenElementType("CONSTRAINT", RmlLanguage.INSTANCE);
        DOWNTO = new ORTokenElementType("DOWNTO", RmlLanguage.INSTANCE);
        INHERIT = new ORTokenElementType("INHERIT", RmlLanguage.INSTANCE);
        INITIALIZER = new ORTokenElementType("INITIALIZER", RmlLanguage.INSTANCE);
        LAND = new ORTokenElementType("LAND", RmlLanguage.INSTANCE);
        LOR = new ORTokenElementType("LOR", RmlLanguage.INSTANCE);
        LSL = new ORTokenElementType("LSL", RmlLanguage.INSTANCE);
        LSR = new ORTokenElementType("LSR", RmlLanguage.INSTANCE);
        LXOR = new ORTokenElementType("LXOR", RmlLanguage.INSTANCE);
        METHOD = new ORTokenElementType("METHOD", RmlLanguage.INSTANCE);
        MOD = new ORTokenElementType("MOD", RmlLanguage.INSTANCE);
        NEW = new ORTokenElementType("NEW", RmlLanguage.INSTANCE);
        NONREC = new ORTokenElementType("NONREC", RmlLanguage.INSTANCE);
        OR = new ORTokenElementType("OR", RmlLanguage.INSTANCE);
        PRIVATE = new ORTokenElementType("PRIVATE", RmlLanguage.INSTANCE);
        VIRTUAL = new ORTokenElementType("VIRTUAL", RmlLanguage.INSTANCE);

        COLON_EQ = new ORTokenElementType("COLON_EQ", RmlLanguage.INSTANCE);
        COLON_GT = new ORTokenElementType("COLON_GT", RmlLanguage.INSTANCE);
        DOTDOT = new ORTokenElementType("DOTDOT", RmlLanguage.INSTANCE);
        SEMISEMI = new ORTokenElementType("SEMISEMI", RmlLanguage.INSTANCE);
        GT_BRACKET = new ORTokenElementType("GT_BRACKET", RmlLanguage.INSTANCE);
        GT_BRACE = new ORTokenElementType("GT_BRACE", RmlLanguage.INSTANCE);
        LEFT_ARROW = new ORTokenElementType("LEFT_ARROW", RmlLanguage.INSTANCE);
        RIGHT_ARROW = new ORTokenElementType("RIGHT_ARROW", RmlLanguage.INSTANCE);

        OBJECT = new ORTokenElementType("OBJECT", RmlLanguage.INSTANCE);
        RECORD = new ORTokenElementType("RECORD", RmlLanguage.INSTANCE);

        AMPERSAND = new ORTokenElementType("AMPERSAND", RmlLanguage.INSTANCE);
        BRACKET_GT = new ORTokenElementType("BRACKET_GT", RmlLanguage.INSTANCE);
        BRACKET_LT = new ORTokenElementType("BRACKET_LT", RmlLanguage.INSTANCE);
        BRACE_LT = new ORTokenElementType("BRACE_LT", RmlLanguage.INSTANCE);

        ML_STRING_VALUE = new ORTokenElementType("ML_STRING_VALUE", RmlLanguage.INSTANCE);
        ML_STRING_OPEN = new ORTokenElementType("ML_STRING_OPEN", RmlLanguage.INSTANCE);
        ML_STRING_CLOSE = new ORTokenElementType("ML_STRING_CLOSE", RmlLanguage.INSTANCE);
        JS_STRING_OPEN = new ORTokenElementType("JS_STRING_OPEN", RmlLanguage.INSTANCE);
        JS_STRING_CLOSE = new ORTokenElementType("JS_STRING_CLOSE", RmlLanguage.INSTANCE);
        UNDERSCORE = new ORTokenElementType("UNDERSCORE", RmlLanguage.INSTANCE);
    }
}
