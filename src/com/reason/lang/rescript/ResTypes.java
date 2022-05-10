package com.reason.lang.rescript;

import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;

public class ResTypes extends ORTypes {
    public static final ResTypes INSTANCE = new ResTypes();

    private ResTypes() {
        // Stub element types

        C_CLASS_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_CLASS_DECLARATION;
        C_FAKE_MODULE = (ORCompositeType) RescriptStubBasedElementTypes.C_FAKE_MODULE;
        C_EXCEPTION_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_EXCEPTION_DECLARATION;
        C_TYPE_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_TYPE_DECLARATION;
        C_EXTERNAL_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_EXTERNAL_DECLARATION;
        C_LET_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_LET_DECLARATION;
        C_MODULE_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_MODULE_DECLARATION;
        C_OBJECT_FIELD = (ORCompositeType) RescriptStubBasedElementTypes.C_OBJECT_FIELD;
        C_VAL_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_VAL_DECLARATION;
        C_FUN_PARAM = (ORCompositeType) RescriptStubBasedElementTypes.C_FUN_PARAM;
        C_FUNCTOR_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_FUNCTOR_DECLARATION;
        C_FUNCTOR_PARAM = (ORCompositeType) RescriptStubBasedElementTypes.C_FUNCTOR_PARAM;
        C_RECORD_FIELD = (ORCompositeType) RescriptStubBasedElementTypes.C_RECORD_FIELD;
        C_VARIANT_DECLARATION = (ORCompositeType) RescriptStubBasedElementTypes.C_VARIANT_DECLARATION;
        C_INCLUDE = (ORCompositeType) RescriptStubBasedElementTypes.C_INCLUDE;
        C_OPEN = (ORCompositeType) RescriptStubBasedElementTypes.C_OPEN;

        // Composite element types

        C_ANNOTATION = new ORCompositeElementType("C_ANNOTATION", ResLanguage.INSTANCE);
        C_MIXIN_FIELD = new ORCompositeElementType("C_MIXIN_FIELD", ResLanguage.INSTANCE);
        C_ASSERT_STMT = new ORCompositeElementType("C_ASSERT_STMT", ResLanguage.INSTANCE);
        C_BINARY_CONDITION = new ORCompositeElementType("C_BIN_CONDITION", ResLanguage.INSTANCE);
        C_CLASS_CONSTR = new ORCompositeElementType("C_CLASS_CONSTR", ResLanguage.INSTANCE);
        C_CLASS_FIELD = new ORCompositeElementType("C_CLASS_FIELD", ResLanguage.INSTANCE);
        C_CLASS_METHOD = new ORCompositeElementType("C_CLASS_METHOD", ResLanguage.INSTANCE);
        C_CONSTRAINTS = new ORCompositeElementType("C_CONSTRAINTS", ResLanguage.INSTANCE);
        C_CONSTRAINT = new ORCompositeElementType("C_CONSTRAINT", ResLanguage.INSTANCE);
        C_CUSTOM_OPERATOR = new ORCompositeElementType("C_CUSTOM_OPERATOR", ResLanguage.INSTANCE);
        C_DECONSTRUCTION = new ORCompositeElementType("C_DECONSTRUCTION", ResLanguage.INSTANCE);
        C_DEFAULT_VALUE = new ORCompositeElementType("C_DEFAULT_VALUE", ResLanguage.INSTANCE);
        C_DIRECTIVE = new ORCompositeElementType("C_DIRECTIVE", ResLanguage.INSTANCE);
        C_DO_LOOP = new ORCompositeElementType("C_DO_LOOP", ResLanguage.INSTANCE);
        C_FOR_LOOP = new ORCompositeElementType("C_FOR_LOOP", ResLanguage.INSTANCE);
        C_LOWER_IDENTIFIER = new ORCompositeElementType("C_LOWER_IDENTIFIER", ResLanguage.INSTANCE);
        C_UPPER_IDENTIFIER = new ORCompositeElementType("C_UPPER_IDENTIFIER", ResLanguage.INSTANCE);
        C_FIELD_VALUE = new ORCompositeElementType("C_FIELD_VALUE", ResLanguage.INSTANCE);
        C_FUN_CALL = new ORCompositeElementType("C_FUN_CALL", ResLanguage.INSTANCE);
        C_FUN_EXPR = new ORCompositeElementType("C_FUN_EXPR", ResLanguage.INSTANCE);
        C_FUN_BODY = new ORCompositeElementType("C_FUN_BODY", ResLanguage.INSTANCE);
        C_FUNCTOR_BINDING = new ORCompositeElementType("C_FUNCTOR_BINDING", ResLanguage.INSTANCE);
        C_FUNCTOR_CALL = new ORCompositeElementType("C_FUNCTOR_CALL", ResLanguage.INSTANCE);
        C_FUNCTOR_RESULT = new ORCompositeElementType("C_FUNCTOR_RESULT", ResLanguage.INSTANCE);
        C_IF = new ORCompositeElementType("C_IF", ResLanguage.INSTANCE);
        C_IF_THEN_SCOPE = new ORCompositeElementType("C_IF_THEN_SCOPE", ResLanguage.INSTANCE);
        C_INTERPOLATION_EXPR = new ORCompositeElementType("C_INTERPOLATION_EXPR", ResLanguage.INSTANCE);
        C_INTERPOLATION_PART = new ORCompositeElementType("C_INTERPOLATION_PART", ResLanguage.INSTANCE);
        C_INTERPOLATION_REF = new ORCompositeElementType("C_INTERPOLATION_REF", ResLanguage.INSTANCE);
        C_JS_OBJECT = new ORCompositeElementType("C_JS_OBJECT", ResLanguage.INSTANCE);
        C_LET_ATTR = new ORCompositeElementType("C_LET_ATTR", ResLanguage.INSTANCE);
        C_LET_BINDING = new ORCompositeElementType("C_LET_BINDING", ResLanguage.INSTANCE);
        C_LOCAL_OPEN = new ORCompositeElementType("C_LOCAL_OPEN", ResLanguage.INSTANCE);
        C_LOWER_BOUND_CONSTRAINT = new ORCompositeElementType("C_LOWER_BOUND_CONSTRAINT", ResLanguage.INSTANCE);
        C_TYPE_VARIABLE = new ORCompositeElementType("C_TYPE_VARIABLE", ResLanguage.INSTANCE);
        C_LOWER_SYMBOL = new ORCompositeElementType("C_LOWER_SYMBOL", ResLanguage.INSTANCE);
        C_MACRO_EXPR = new ORCompositeElementType("C_MACRO_EXPR", ResLanguage.INSTANCE);
        C_MACRO_NAME = new ORCompositeElementType("C_MACRO_NAME", ResLanguage.INSTANCE);
        C_MACRO_BODY = new ORCompositeElementType("C_MACRO_RAW_BODY", ResLanguage.INSTANCE);
        C_MODULE_TYPE = new ORCompositeElementType("C_MODULE_TYPE", ResLanguage.INSTANCE);
        C_MODULE_BINDING = new ORCompositeElementType("C_MODULE_BINDING", ResLanguage.INSTANCE);
        C_ML_INTERPOLATOR = new ORCompositeElementType("C_ML_INTERPOLATOR", ResLanguage.INSTANCE);
        C_NAMED_PARAM = new ORCompositeElementType("C_NAMED_PARAM", ResLanguage.INSTANCE);
        C_OBJECT = new ORCompositeElementType("C_OBJECT", ResLanguage.INSTANCE);
        C_OPTION = new ORCompositeElementType("C_OPTION", ResLanguage.INSTANCE);
        C_PARAMETERS = new ORCompositeElementType("C_PARAMETERS", ResLanguage.INSTANCE);
        C_PATTERN_MATCH_BODY = new ORCompositeElementType("C_PATTERN_MATCH_BODY", ResLanguage.INSTANCE);
        C_PATTERN_MATCH_EXPR = new ORCompositeElementType("C_PATTERN_MATCH_EXPR", ResLanguage.INSTANCE);
        C_RECORD_EXPR = new ORCompositeElementType("C_RECORD_EXPR", ResLanguage.INSTANCE);
        C_SIG_EXPR = new ORCompositeElementType("C_SIG_EXPR", ResLanguage.INSTANCE);
        C_SIG_ITEM = new ORCompositeElementType("C_SIG_ITEM", ResLanguage.INSTANCE);
        C_SCOPED_EXPR = new ORCompositeElementType("C_SCOPED_EXPR", ResLanguage.INSTANCE);
        C_STRUCT_EXPR = new ORCompositeElementType("C_STRUCT_EXPR", ResLanguage.INSTANCE);
        C_SWITCH_BODY = new ORCompositeElementType("C_SWITCH_BODY", ResLanguage.INSTANCE);
        C_SWITCH_EXPR = new ORCompositeElementType("C_SWITCH_EXPR", ResLanguage.INSTANCE);
        C_TAG = new ORCompositeElementType("C_TAG", ResLanguage.INSTANCE);
        C_TAG_PROP_VALUE = new ORCompositeElementType("C_TAG_PROP_VALUE", ResLanguage.INSTANCE);
        C_TAG_BODY = new ORCompositeElementType("C_TAG_BODY", ResLanguage.INSTANCE);
        C_TAG_START = new ORCompositeElementType("C_TAG_START", ResLanguage.INSTANCE);
        C_TAG_CLOSE = new ORCompositeElementType("C_TAG_CLOSE", ResLanguage.INSTANCE);
        C_TAG_PROPERTY = new ORCompositeElementType("C_TAG_PROPERTY", ResLanguage.INSTANCE);
        C_TERNARY = new ORCompositeElementType("C_TERNARY", ResLanguage.INSTANCE);
        C_TRY_EXPR = new ORCompositeElementType("C_TRY_EXPR", ResLanguage.INSTANCE);
        C_TRY_BODY = new ORCompositeElementType("C_TRY_BODY", ResLanguage.INSTANCE);
        C_TRY_HANDLER = new ORCompositeElementType("C_TRY_HANDLER", ResLanguage.INSTANCE);
        C_TRY_HANDLERS = new ORCompositeElementType("C_TRY_HANDLERS", ResLanguage.INSTANCE);
        C_TUPLE = new ORCompositeElementType("C_TUPLE", ResLanguage.INSTANCE);
        C_TYPE_BINDING = new ORCompositeElementType("C_TYPE_BINDING", ResLanguage.INSTANCE);
        C_UNIT = new ORCompositeElementType("C_UNIT", ResLanguage.INSTANCE);
        C_DUMMY = new ORCompositeElementType("C_DUMMY", ResLanguage.INSTANCE);
        C_UPPER_SYMBOL = new ORCompositeElementType("C_UPPER_SYMBOL", ResLanguage.INSTANCE);
        C_UPPER_BOUND_CONSTRAINT = new ORCompositeElementType("C_UPPER_BOUND_CONSTRAINT", ResLanguage.INSTANCE);
        C_VARIANT = new ORCompositeElementType("C_VARIANT", ResLanguage.INSTANCE);
        C_VARIANT_CONSTRUCTOR = new ORCompositeElementType("C_VARIANT_CONSTRUCTOR", ResLanguage.INSTANCE);
        C_WHILE = new ORCompositeElementType("C_WHILE", ResLanguage.INSTANCE);

        // Token element types

        AND = new ORTokenElementType("AND", ResLanguage.INSTANCE);
        L_AND = new ORTokenElementType("L_AND", ResLanguage.INSTANCE);
        L_OR = new ORTokenElementType("L_OR", ResLanguage.INSTANCE);
        ARROBASE = new ORTokenElementType("ARROBASE", ResLanguage.INSTANCE);
        ARROBASE_2 = new ORTokenElementType("ARROBASE_2", ResLanguage.INSTANCE);
        ARROBASE_3 = new ORTokenElementType("ARROBASE_3", ResLanguage.INSTANCE);
        ARROW = new ORTokenElementType("ARROW", ResLanguage.INSTANCE);
        ASSERT = new ORTokenElementType("ASSERT", ResLanguage.INSTANCE);
        AS = new ORTokenElementType("AS", ResLanguage.INSTANCE);
        BACKSLASH = new ORTokenElementType("BACKSLASH", ResLanguage.INSTANCE);
        BOOL_VALUE = new ORTokenElementType("BOOL_VALUE", ResLanguage.INSTANCE);
        CATCH = new ORTokenElementType("CATCH", ResLanguage.INSTANCE);
        CHAR_VALUE = new ORTokenElementType("CHAR_VALUE", ResLanguage.INSTANCE);
        EXCEPTION_NAME = new ORTokenElementType("EXCEPTION_NAME", ResLanguage.INSTANCE);
        FLOAT_VALUE = new ORTokenElementType("FLOAT_VALUE", ResLanguage.INSTANCE);
        FUNCTION = new ORTokenElementType("FUNCTION", ResLanguage.INSTANCE);
        FUN = new ORTokenElementType("FUN", ResLanguage.INSTANCE);
        FUNCTOR = new ORTokenElementType("FUNCTOR", ResLanguage.INSTANCE);
        INT_VALUE = new ORTokenElementType("INT_VALUE", ResLanguage.INSTANCE);
        PROPERTY_NAME = new ORTokenElementType("PROPERTY_NAME", ResLanguage.INSTANCE);
        STRING_VALUE = new ORTokenElementType("STRING_VALUE", ResLanguage.INSTANCE);
        SWITCH = new ORTokenElementType("SWITCH", ResLanguage.INSTANCE);
        IF = new ORTokenElementType("IF", ResLanguage.INSTANCE);
        BACKTICK = new ORTokenElementType("BACKTICK", ResLanguage.INSTANCE);
        BEGIN = new ORTokenElementType("BEGIN", ResLanguage.INSTANCE);
        CARRET = new ORTokenElementType("CARRET", ResLanguage.INSTANCE);
        COLON = new ORTokenElementType("COLON", ResLanguage.INSTANCE);
        COMMA = new ORTokenElementType("COMMA", ResLanguage.INSTANCE);
        SINGLE_COMMENT = new ORTokenElementType("SINGLE_COMMENT", ResLanguage.INSTANCE);
        MULTI_COMMENT = new ORTokenElementType("MULTI_COMMENT", ResLanguage.INSTANCE);
        DIFF = new ORTokenElementType("DIFF", ResLanguage.INSTANCE);
        DIRECTIVE_IF = new ORTokenElementType("DIRECTIVE_IF", ResLanguage.INSTANCE);
        DIRECTIVE_ELSE = new ORTokenElementType("DIRECTIVE_ELSE", ResLanguage.INSTANCE);
        DIRECTIVE_ELIF = new ORTokenElementType("DIRECTIVE_ELIF", ResLanguage.INSTANCE);
        DIRECTIVE_END = new ORTokenElementType("DIRECTIVE_END", ResLanguage.INSTANCE);
        DIRECTIVE_ENDIF = new ORTokenElementType("DIRECTIVE_ENDIF", ResLanguage.INSTANCE);
        LT_OR_EQUAL = new ORTokenElementType("LT_OR_EQUAL", ResLanguage.INSTANCE);
        GT_OR_EQUAL = new ORTokenElementType("GT_OR_EQUAL", ResLanguage.INSTANCE);
        DOLLAR = new ORTokenElementType("DOLLAR", ResLanguage.INSTANCE);
        DOT = new ORTokenElementType("DOT", ResLanguage.INSTANCE);
        DOTDOTDOT = new ORTokenElementType("DOTDOTDOT", ResLanguage.INSTANCE);
        DO = new ORTokenElementType("DO", ResLanguage.INSTANCE);
        DONE = new ORTokenElementType("DONE", ResLanguage.INSTANCE);
        EOL = new ORTokenElementType("EOL", ResLanguage.INSTANCE);
        ELSE = new ORTokenElementType("ELSE", ResLanguage.INSTANCE);
        END = new ORTokenElementType("END", ResLanguage.INSTANCE);
        ENDIF = new ORTokenElementType("ENDIF", ResLanguage.INSTANCE);
        NOT_EQ = new ORTokenElementType("EQ", ResLanguage.INSTANCE);
        NOT_EQEQ = new ORTokenElementType("EQEQ", ResLanguage.INSTANCE);
        EQ = new ORTokenElementType("EQ", ResLanguage.INSTANCE);
        EQEQ = new ORTokenElementType("EQEQ", ResLanguage.INSTANCE);
        EQEQEQ = new ORTokenElementType("EQEQEQ", ResLanguage.INSTANCE);
        EXCEPTION = new ORTokenElementType("EXCEPTION", ResLanguage.INSTANCE);
        EXCLAMATION_MARK = new ORTokenElementType("EXCLAMATION_MARK", ResLanguage.INSTANCE);
        EXTERNAL = new ORTokenElementType("EXTERNAL", ResLanguage.INSTANCE);
        FOR = new ORTokenElementType("FOR", ResLanguage.INSTANCE);
        TYPE_ARGUMENT = new ORTokenElementType("TYPE_ARGUMENT", ResLanguage.INSTANCE);
        GT = new ORTokenElementType("GT", ResLanguage.INSTANCE);
        IN = new ORTokenElementType("IN", ResLanguage.INSTANCE);
        LAZY = new ORTokenElementType("LAZY", ResLanguage.INSTANCE);
        INCLUDE = new ORTokenElementType("INCLUDE", ResLanguage.INSTANCE);
        LARRAY = new ORTokenElementType("LARRAY", ResLanguage.INSTANCE);
        LBRACE = new ORTokenElementType("LBRACE", ResLanguage.INSTANCE);
        LBRACKET = new ORTokenElementType("LBRACKET", ResLanguage.INSTANCE);
        LET = new ORTokenElementType("LET", ResLanguage.INSTANCE);
        LIDENT = new ORTokenElementType("LIDENT", ResLanguage.INSTANCE);
        LPAREN = new ORTokenElementType("LPAREN", ResLanguage.INSTANCE);
        LT = new ORTokenElementType("LT", ResLanguage.INSTANCE);
        MATCH = new ORTokenElementType("MATCH", ResLanguage.INSTANCE);
        MINUS = new ORTokenElementType("MINUS", ResLanguage.INSTANCE);
        MINUSDOT = new ORTokenElementType("MINUSDOT", ResLanguage.INSTANCE);
        MODULE = new ORTokenElementType("MODULE", ResLanguage.INSTANCE);
        MUTABLE = new ORTokenElementType("MUTABLE", ResLanguage.INSTANCE);
        NONE = new ORTokenElementType("NONE", ResLanguage.INSTANCE);
        OF = new ORTokenElementType("OF", ResLanguage.INSTANCE);
        OPEN = new ORTokenElementType("OPEN", ResLanguage.INSTANCE);
        OPTION = new ORTokenElementType("OPTION", ResLanguage.INSTANCE);
        POLY_VARIANT = new ORTokenElementType("POLY_VARIANT", ResLanguage.INSTANCE);
        VARIANT_NAME = new ORTokenElementType("VARIANT_NAME", ResLanguage.INSTANCE);
        PIPE = new ORTokenElementType("PIPE", ResLanguage.INSTANCE);
        PIPE_FORWARD = new ORTokenElementType("PIPE_FORWARD", ResLanguage.INSTANCE);
        PLUS = new ORTokenElementType("PLUS", ResLanguage.INSTANCE);
        PERCENT = new ORTokenElementType("PERCENT", ResLanguage.INSTANCE);
        PLUSDOT = new ORTokenElementType("PLUSDOT", ResLanguage.INSTANCE);
        QUESTION_MARK = new ORTokenElementType("QUESTION_MARK", ResLanguage.INSTANCE);
        SINGLE_QUOTE = new ORTokenElementType("SINGLE_QUOTE", ResLanguage.INSTANCE);
        DOUBLE_QUOTE = new ORTokenElementType("DOUBLE_QUOTE", ResLanguage.INSTANCE);
        RAISE = new ORTokenElementType("RAISE", ResLanguage.INSTANCE);
        RARRAY = new ORTokenElementType("RARRAY", ResLanguage.INSTANCE);
        RBRACE = new ORTokenElementType("RBRACE", ResLanguage.INSTANCE);
        RBRACKET = new ORTokenElementType("RBRACKET", ResLanguage.INSTANCE);
        REC = new ORTokenElementType("REC", ResLanguage.INSTANCE);
        REF = new ORTokenElementType("REF", ResLanguage.INSTANCE);
        RPAREN = new ORTokenElementType("RPAREN", ResLanguage.INSTANCE);
        SEMI = new ORTokenElementType("SEMI", ResLanguage.INSTANCE);
        SIG = new ORTokenElementType("SIG", ResLanguage.INSTANCE);
        SHARP = new ORTokenElementType("SHARP", ResLanguage.INSTANCE);
        SHARPSHARP = new ORTokenElementType("SHARPSHARP", ResLanguage.INSTANCE);
        SHORTCUT = new ORTokenElementType("SHORTCUT", ResLanguage.INSTANCE);
        SLASH = new ORTokenElementType("SLASH", ResLanguage.INSTANCE);
        SLASH_2 = new ORTokenElementType("SLASH_2", ResLanguage.INSTANCE);
        SLASHDOT = new ORTokenElementType("SLASHDOT", ResLanguage.INSTANCE);
        SOME = new ORTokenElementType("SOME", ResLanguage.INSTANCE);
        STAR = new ORTokenElementType("STAR", ResLanguage.INSTANCE);
        STARDOT = new ORTokenElementType("STARDOT", ResLanguage.INSTANCE);
        STRUCT = new ORTokenElementType("STRUCT", ResLanguage.INSTANCE);
        OP_STRUCT_DIFF = new ORTokenElementType("OP_STRUCT_DIFF", ResLanguage.INSTANCE);
        TAG_AUTO_CLOSE = new ORTokenElementType("TAG_AUTO_CLOSE", ResLanguage.INSTANCE);
        TAG_NAME = new ORTokenElementType("TAG_NAME", ResLanguage.INSTANCE);
        TAG_LT_SLASH = new ORTokenElementType("TAG_LT_SLASH", ResLanguage.INSTANCE);
        TILDE = new ORTokenElementType("TILDE", ResLanguage.INSTANCE);
        TO = new ORTokenElementType("TO", ResLanguage.INSTANCE);
        THEN = new ORTokenElementType("THEN", ResLanguage.INSTANCE);
        TRY = new ORTokenElementType("TRY", ResLanguage.INSTANCE);
        TYPE = new ORTokenElementType("TYPE", ResLanguage.INSTANCE);
        UIDENT = new ORTokenElementType("UIDENT", ResLanguage.INSTANCE);
        UNIT = new ORTokenElementType("UNIT", ResLanguage.INSTANCE);
        VAL = new ORTokenElementType("VAL", ResLanguage.INSTANCE);
        PUB = new ORTokenElementType("PUB", ResLanguage.INSTANCE);
        PRI = new ORTokenElementType("PRI", ResLanguage.INSTANCE);
        WHEN = new ORTokenElementType("WHEN", ResLanguage.INSTANCE);
        WHILE = new ORTokenElementType("WHILE", ResLanguage.INSTANCE);
        WITH = new ORTokenElementType("WITH", ResLanguage.INSTANCE);
        RAW = new ORTokenElementType("RAW", ResLanguage.INSTANCE);

        ASR = new ORTokenElementType("ASR", ResLanguage.INSTANCE);
        CLASS = new ORTokenElementType("CLASS", ResLanguage.INSTANCE);
        CONSTRAINT = new ORTokenElementType("CONSTRAINT", ResLanguage.INSTANCE);
        DOWNTO = new ORTokenElementType("DOWNTO", ResLanguage.INSTANCE);
        INHERIT = new ORTokenElementType("INHERIT", ResLanguage.INSTANCE);
        INITIALIZER = new ORTokenElementType("INITIALIZER", ResLanguage.INSTANCE);
        LAND = new ORTokenElementType("LAND", ResLanguage.INSTANCE);
        LOR = new ORTokenElementType("LOR", ResLanguage.INSTANCE);
        LSL = new ORTokenElementType("LSL", ResLanguage.INSTANCE);
        LSR = new ORTokenElementType("LSR", ResLanguage.INSTANCE);
        LXOR = new ORTokenElementType("LXOR", ResLanguage.INSTANCE);
        METHOD = new ORTokenElementType("METHOD", ResLanguage.INSTANCE);
        MOD = new ORTokenElementType("MOD", ResLanguage.INSTANCE);
        NEW = new ORTokenElementType("NEW", ResLanguage.INSTANCE);
        NONREC = new ORTokenElementType("NONREC", ResLanguage.INSTANCE);
        OR = new ORTokenElementType("OR", ResLanguage.INSTANCE);
        PRIVATE = new ORTokenElementType("PRIVATE", ResLanguage.INSTANCE);
        VIRTUAL = new ORTokenElementType("VIRTUAL", ResLanguage.INSTANCE);

        COLON_EQ = new ORTokenElementType("COLON_EQ", ResLanguage.INSTANCE);
        COLON_GT = new ORTokenElementType("COLON_GT", ResLanguage.INSTANCE);
        DOTDOT = new ORTokenElementType("DOTDOT", ResLanguage.INSTANCE);
        SEMISEMI = new ORTokenElementType("SEMISEMI", ResLanguage.INSTANCE);
        GT_BRACKET = new ORTokenElementType("GT_BRACKET", ResLanguage.INSTANCE);
        GT_BRACE = new ORTokenElementType("GT_BRACE", ResLanguage.INSTANCE);
        LEFT_ARROW = new ORTokenElementType("LEFT_ARROW", ResLanguage.INSTANCE);
        RIGHT_ARROW = new ORTokenElementType("RIGHT_ARROW", ResLanguage.INSTANCE);

        OBJECT = new ORTokenElementType("OBJECT", ResLanguage.INSTANCE);
        RECORD = new ORTokenElementType("RECORD", ResLanguage.INSTANCE);

        AMPERSAND = new ORTokenElementType("AMPERSAND", ResLanguage.INSTANCE);
        BRACKET_GT = new ORTokenElementType("BRACKET_GT", ResLanguage.INSTANCE);
        BRACKET_LT = new ORTokenElementType("BRACKET_LT", ResLanguage.INSTANCE);
        BRACE_LT = new ORTokenElementType("BRACE_LT", ResLanguage.INSTANCE);

        ML_STRING_VALUE = new ORTokenElementType("ML_STRING_VALUE", ResLanguage.INSTANCE);
        ML_STRING_OPEN = new ORTokenElementType("ML_STRING_OPEN", ResLanguage.INSTANCE);
        ML_STRING_CLOSE = new ORTokenElementType("ML_STRING_CLOSE", ResLanguage.INSTANCE);
        JS_STRING_OPEN = new ORTokenElementType("JS_STRING_OPEN", ResLanguage.INSTANCE);
        JS_STRING_CLOSE = new ORTokenElementType("JS_STRING_CLOSE", ResLanguage.INSTANCE);
        UNDERSCORE = new ORTokenElementType("UNDERSCORE", ResLanguage.INSTANCE);
    }
}
