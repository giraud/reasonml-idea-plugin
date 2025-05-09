package com.reason.lang.core.type;

public abstract class ORLangTypes extends ORTypes {
    // Stubbed elements

    public ORCompositeType C_CLASS_DECLARATION;
    public ORCompositeType C_CLASS_METHOD;
    public ORCompositeType C_EXCEPTION_DECLARATION;
    public ORCompositeType C_EXTERNAL_DECLARATION;
    public ORCompositeType C_FUNCTOR_DECLARATION;
    public ORCompositeType C_INCLUDE;
    public ORCompositeType C_LET_DECLARATION;
    public ORCompositeType C_MODULE_DECLARATION;
    public ORCompositeType C_OBJECT_FIELD;
    public ORCompositeType C_OPEN;
    public ORCompositeType C_PARAM_DECLARATION;
    public ORCompositeType C_RECORD_FIELD;
    public ORCompositeType C_TYPE_DECLARATION;
    public ORCompositeType C_VAL_DECLARATION;
    public ORCompositeType C_VARIANT_DECLARATION;

    // Composite element types

    public ORCompositeType C_ANNOTATION;
    public ORCompositeType C_ARRAY;
    public ORCompositeType C_ASSERT_STMT;
    public ORCompositeType C_BINARY_CONDITION;
    public ORCompositeType C_CLASS_CONSTR;
    public ORCompositeType C_CLASS_FIELD;
    public ORCompositeType C_CLASS_INITIALIZER;
    public ORCompositeType C_CLOSED_VARIANT;
    public ORCompositeType C_CONSTRAINTS;
    public ORCompositeType C_TYPE_CONSTRAINT;
    public ORCompositeType C_CUSTOM_OPERATOR;
    public ORCompositeType C_DECONSTRUCTION;
    public ORCompositeType C_DEFAULT_VALUE;
    public ORCompositeType C_DIRECTIVE;
    public ORCompositeType C_DO_LOOP;
    public ORCompositeType C_FIELD_VALUE;
    public ORCompositeType C_FIRST_CLASS;
    public ORCompositeType C_FOR_LOOP;
    public ORCompositeType C_FUN_EXPR;
    public ORCompositeType C_FUNCTION_BODY;
    public ORCompositeType C_FUNCTION_CALL;
    public ORCompositeType C_FUNCTION_EXPR;
    public ORCompositeType C_FUNCTOR_BINDING;
    public ORCompositeType C_FUNCTOR_CALL;
    public ORCompositeType C_FUNCTOR_RESULT;
    public ORCompositeType C_GUARD;
    public ORCompositeType C_IF;
    public ORCompositeType C_IF_THEN_ELSE;
    public ORCompositeType C_INHERIT;
    public ORCompositeType C_LET_ATTR;
    public ORCompositeType C_LET_BINDING;
    public ORCompositeType C_LOCAL_OPEN;
    public ORCompositeType C_TYPE_VARIABLE;
    public ORCompositeType C_MACRO_EXPR;
    public ORCompositeType C_MACRO_NAME;
    public ORCompositeType C_MACRO_BODY;
    public ORCompositeType C_METHOD_CALL;
    public ORCompositeType C_MODULE_BINDING;
    public ORCompositeType C_MODULE_SIGNATURE;
    public ORCompositeType C_JS_OBJECT;
    public ORCompositeType C_MATCH_EXPR;
    public ORCompositeType C_MIXIN_FIELD;
    public ORCompositeType C_ML_INTERPOLATOR;
    public ORCompositeType C_NAMED_PARAM;
    public ORCompositeType C_NONE;
    public ORCompositeType C_OBJECT;
    public ORCompositeType C_OPEN_VARIANT;
    public ORCompositeType C_OPTION;
    public ORCompositeType C_LOWER_NAME;
    public ORCompositeType C_PARAM;
    public ORCompositeType C_PARAMETERS;
    public ORCompositeType C_PATTERN_MATCH_BODY;
    public ORCompositeType C_PATTERN_MATCH_EXPR;
    public ORCompositeType C_SCOPED_EXPR;
    public ORCompositeType C_SIG_EXPR;
    public ORCompositeType C_SIG_ITEM;
    public ORCompositeType C_SOME;
    public ORCompositeType C_TAG;
    public ORCompositeType C_TAG_PROP_VALUE;
    public ORCompositeType C_TAG_BODY;
    public ORCompositeType C_INTERPOLATION_EXPR;
    public ORCompositeType C_INTERPOLATION_PART;
    public ORCompositeType C_INTERPOLATION_REF;
    public ORCompositeType C_TAG_START;
    public ORCompositeType C_TAG_CLOSE;
    public ORCompositeType C_TAG_PROPERTY;
    public ORCompositeType C_TERNARY;
    public ORCompositeType C_TUPLE;
    public ORCompositeType C_RECORD_EXPR;
    public ORCompositeType C_SWITCH_EXPR;
    public ORCompositeType C_SWITCH_BODY;
    public ORCompositeType C_STRUCT_EXPR;
    public ORCompositeType C_TRY_EXPR;
    public ORCompositeType C_TRY_BODY;
    public ORCompositeType C_TRY_HANDLERS;
    public ORCompositeType C_TRY_HANDLER;
    public ORCompositeType C_TRY_HANDLER_BODY;
    public ORCompositeType C_TYPE_BINDING;
    public ORCompositeType C_UNIT;
    public ORCompositeType C_UNPACK;
    public ORCompositeType C_VARIANT_CONSTRUCTOR;
    public ORCompositeType C_WHILE;

    // Atom types

    public ORCompositeType CA_LOWER_SYMBOL;
    public ORCompositeType CA_UPPER_SYMBOL;
    public ORTokenElementType A_LOWER_TAG_NAME;
    public ORTokenElementType A_UPPER_TAG_NAME;
    public ORTokenElementType A_VARIANT_NAME;
    public ORTokenElementType A_MODULE_NAME;
    public ORTokenElementType A_EXCEPTION_NAME;

    // Dummy types

    public ORCompositeType H_ATOM;
    public ORCompositeType H_PLACE_HOLDER;
    public ORCompositeType H_COLLECTION_ITEM;
    public ORCompositeType H_NAMED_PARAM_DECLARATION;

    // Token element types

    public ORTokenElementType BOOL_VALUE;
    public ORTokenElementType STRING_VALUE;
    public ORTokenElementType FLOAT_VALUE;
    public ORTokenElementType CHAR_VALUE;
    public ORTokenElementType INT_VALUE;

    public ORTokenElementType ASYNC;
    public ORTokenElementType AWAIT;
    public ORTokenElementType AND;
    public ORTokenElementType ASSERT;
    public ORTokenElementType BACKSLASH;
    public ORTokenElementType BEGIN;
    public ORTokenElementType CATCH;
    public ORTokenElementType CLASS;
    public ORTokenElementType CONSTRAINT;
    public ORTokenElementType DIRECTIVE_IF;
    public ORTokenElementType DIRECTIVE_ELSE;
    public ORTokenElementType DIRECTIVE_ELIF;
    public ORTokenElementType DIRECTIVE_END;
    public ORTokenElementType DIRECTIVE_ENDIF;
    public ORTokenElementType DO;
    public ORTokenElementType DONE;
    public ORTokenElementType DOWNTO;
    public ORTokenElementType ELSE;
    public ORTokenElementType END;
    public ORTokenElementType EXCEPTION;
    public ORTokenElementType EXTERNAL;
    public ORTokenElementType FOR;
    public ORTokenElementType FUN;
    public ORTokenElementType FUNCTION;
    public ORTokenElementType FUNCTOR;
    public ORTokenElementType IF;
    public ORTokenElementType IN;
    public ORTokenElementType INCLUDE;
    public ORTokenElementType INHERIT;
    public ORTokenElementType INITIALIZER;
    public ORTokenElementType LAZY;
    public ORTokenElementType LET;
    public ORTokenElementType LIST;
    public ORTokenElementType L_AND;
    public ORTokenElementType OP_STRUCT_DIFF;
    public ORTokenElementType L_OR;
    public ORTokenElementType MODULE;
    public ORTokenElementType MUTABLE;
    public ORTokenElementType NEW;
    public ORTokenElementType NONREC;
    public ORTokenElementType OBJECT;
    public ORTokenElementType OF;
    public ORTokenElementType OPEN;
    public ORTokenElementType OR;
    public ORTokenElementType PIPE_FIRST;
    public ORTokenElementType PUB;
    public ORTokenElementType PRI;
    public ORTokenElementType REC;
    public ORTokenElementType SIG;
    public ORTokenElementType STRING_CONCAT;
    public ORTokenElementType STRUCT;
    public ORTokenElementType SWITCH;
    public ORTokenElementType THEN;
    public ORTokenElementType TO;
    public ORTokenElementType TRY;
    public ORTokenElementType TYPE;
    public ORTokenElementType UNPACK; // rescript
    public ORTokenElementType VAL;
    public ORTokenElementType VIRTUAL;
    public ORTokenElementType WHEN;
    public ORTokenElementType WHILE;
    public ORTokenElementType WITH;
    public ORTokenElementType RAW;
    public ORTokenElementType PROPERTY_NAME;
    public ORTokenElementType SHARPSHARP;
    public ORTokenElementType ARROBASE;
    public ORTokenElementType ARROBASE_2;
    public ORTokenElementType ARROBASE_3;
    public ORTokenElementType ARROW;
    public ORTokenElementType AS;
    public ORTokenElementType BACKTICK;
    public ORTokenElementType CARRET;
    public ORTokenElementType COLON;
    public ORTokenElementType COMMA;
    public ORTokenElementType DIFF;
    public ORTokenElementType LT_OR_EQUAL;
    public ORTokenElementType GT_OR_EQUAL;
    public ORTokenElementType DOLLAR;
    public ORTokenElementType DOT;
    public ORTokenElementType DOTDOTDOT;
    public ORTokenElementType NOT_EQ;
    public ORTokenElementType NOT_EQEQ;
    public ORTokenElementType EOL;
    public ORTokenElementType EQ;
    public ORTokenElementType EQEQ;
    public ORTokenElementType EQEQEQ;
    public ORTokenElementType EXCLAMATION_MARK;
    public ORTokenElementType TYPE_ARGUMENT;
    public ORTokenElementType GT;
    public ORTokenElementType LARRAY;
    public ORTokenElementType LBRACE;
    public ORTokenElementType LBRACKET;
    public ORTokenElementType LIDENT;
    public ORTokenElementType LPAREN;
    public ORTokenElementType LT;
    public ORTokenElementType MATCH;
    public ORTokenElementType MINUS;
    public ORTokenElementType MINUSDOT;
    public ORTokenElementType NONE;
    public ORTokenElementType OPTION;
    public ORTokenElementType POLY_VARIANT;
    public ORTokenElementType PIPE;
    public ORTokenElementType PIPE_FORWARD;
    public ORTokenElementType PLUS;
    public ORTokenElementType PERCENT;
    public ORTokenElementType PLUSDOT;
    public ORTokenElementType QUESTION_MARK;
    public ORTokenElementType SINGLE_QUOTE;
    public ORTokenElementType DOUBLE_QUOTE;
    public ORTokenElementType RAISE;
    public ORTokenElementType RARRAY;
    public ORTokenElementType RBRACE;
    public ORTokenElementType RBRACKET;
    public ORTokenElementType REF;
    public ORTokenElementType RPAREN;
    public ORTokenElementType SEMI;
    public ORTokenElementType SHARP;
    public ORTokenElementType SHORTCUT;
    public ORTokenElementType SLASH;
    public ORTokenElementType SLASH_2;
    public ORTokenElementType SLASHDOT;
    public ORTokenElementType SOME;
    public ORTokenElementType STAR;
    public ORTokenElementType STARDOT;
    public ORTokenElementType TAG_AUTO_CLOSE;
    public ORTokenElementType TAG_LT_SLASH;
    public ORTokenElementType TILDE;
    public ORTokenElementType UIDENT;
    public ORTokenElementType UNIT;
    public ORTokenElementType RECORD;
    public ORTokenElementType ASR;
    public ORTokenElementType LAND;
    public ORTokenElementType LOR;
    public ORTokenElementType LSL;
    public ORTokenElementType LSR;
    public ORTokenElementType LXOR;
    public ORTokenElementType METHOD;
    public ORTokenElementType MOD;
    public ORTokenElementType PRIVATE;
    public ORTokenElementType COLON_EQ;
    public ORTokenElementType COLON_GT;
    public ORTokenElementType DOTDOT;
    public ORTokenElementType SEMISEMI;
    public ORTokenElementType GT_BRACKET;
    public ORTokenElementType GT_BRACE;
    public ORTokenElementType LEFT_ARROW;
    public ORTokenElementType RIGHT_ARROW;
    public ORTokenElementType AMPERSAND;
    public ORTokenElementType BRACKET_GT;
    public ORTokenElementType BRACKET_LT;
    public ORTokenElementType BRACE_LT;
    public ORTokenElementType ML_STRING_VALUE;
    public ORTokenElementType ML_STRING_OPEN;
    public ORTokenElementType ML_STRING_CLOSE;
    public ORTokenElementType JS_STRING_OPEN;
    public ORTokenElementType JS_STRING_CLOSE;
    public ORTokenElementType UNDERSCORE;
}
