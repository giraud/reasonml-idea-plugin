package com.reason.lang.core.type;

import com.intellij.psi.tree.IElementType;

public abstract class ORTypes {

    // Composite element types

    public IElementType C_ASSERT_STMT;
    public IElementType C_CLASS_CONSTR;
    public IElementType C_CLASS_FIELD;
    public IElementType C_CLASS_METHOD;
    public IElementType C_CLASS_PARAMS;
    public IElementType C_CLASS_STMT;
    public IElementType C_DECONSTRUCTION;
    public IElementType C_DIRECTIVE;
    public IElementType C_EXTERNAL_STMT;
    public IElementType C_EXP_TYPE;
    public IElementType C_FAKE_MODULE;
    public IElementType C_LET_STMT;
    public IElementType C_MODULE_STMT;
    public IElementType C_VAL_EXPR;
    public IElementType C_ANNOTATION_EXPR;
    public IElementType C_EXCEPTION_EXPR;
    public IElementType C_OPEN;
    public IElementType C_INCLUDE;
    public IElementType C_LET_BINDING;
    public IElementType C_MACRO_EXPR;
    public IElementType C_MACRO_NAME;
    public IElementType C_FUN_CALL_PARAMS;
    public IElementType C_FUN_CALL_PARAM;
    public IElementType C_FUN_EXPR;
    public IElementType C_FUN_PARAMS;
    public IElementType C_FUN_PARAM;
    public IElementType C_FUN_PARAM_BINDING;
    public IElementType C_FUN_BODY;
    public IElementType C_FUNCTOR;
    public IElementType C_FUNCTOR_BINDING;
    public IElementType C_FUNCTOR_CONSTRAINTS;
    public IElementType C_FUNCTOR_PARAMS;
    public IElementType C_FUNCTOR_PARAM;
    public IElementType C_JS_OBJECT;
    public IElementType C_MACRO_RAW_BODY;
    public IElementType C_MIXIN_FIELD;
    public IElementType C_ML_INTERPOLATOR;
    public IElementType C_MODULE_PATH;
    public IElementType C_OBJECT;
    public IElementType C_OBJECT_FIELD;
    public IElementType C_OPTION;
    public IElementType C_PATTERN_MATCH_BODY;
    public IElementType C_PATTERN_MATCH_EXPR;
    public IElementType C_RAW;
    public IElementType C_SIG_EXPR;
    public IElementType C_SIG_ITEM;
    public IElementType C_TAG;
    public IElementType C_TAG_PROP_VALUE;
    public IElementType C_TAG_BODY;
    public IElementType C_UNIT;
    public IElementType C_UNKNOWN_EXPR;
    public IElementType C_VARIANT;
    public IElementType C_VARIANT_DECL;
    public IElementType C_VARIANT_CONSTRUCTOR;
    public IElementType C_SCOPED_EXPR;
    public IElementType C_IF_STMT;
    public IElementType C_BIN_CONDITION;
    public IElementType C_INTERPOLATION_EXPR;
    public IElementType C_INTERPOLATION_PART;
    public IElementType C_INTERPOLATION_REF;
    public IElementType C_TAG_START;
    public IElementType C_TAG_CLOSE;
    public IElementType C_TAG_PROPERTY;
    public IElementType C_RECORD_EXPR;
    public IElementType C_RECORD_FIELD;
    public IElementType C_SWITCH_EXPR;
    public IElementType C_MATCH_EXPR;
    public IElementType C_TRY_EXPR;
    public IElementType C_TRY_BODY;
    public IElementType C_TRY_HANDLERS;
    public IElementType C_TRY_HANDLER;
    public IElementType C_TYPE_CONSTR_NAME;
    public IElementType C_TYPE_BINDING;
    public IElementType C_UPPER_SYMBOL;
    public IElementType C_LOWER_SYMBOL;
    public IElementType C_STRUCT_EXPR;
    public IElementType C_WHILE;
    public IElementType C_WHILE_CONDITION;

    // Token element types

    public ORTokenElementType BOOL_VALUE;
    public ORTokenElementType STRING_VALUE;
    public ORTokenElementType FLOAT_VALUE;
    public ORTokenElementType CHAR_VALUE;
    public ORTokenElementType INT_VALUE;

    public ORTokenElementType AND;
    public ORTokenElementType ANDAND;
    public ORTokenElementType ASSERT;
    public ORTokenElementType BEGIN;
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
    public ORTokenElementType ENDIF;
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
    public ORTokenElementType MODULE;
    public ORTokenElementType MUTABLE;
    public ORTokenElementType NEW;
    public ORTokenElementType NONREC;
    public ORTokenElementType OBJECT;
    public ORTokenElementType OF;
    public ORTokenElementType OPEN;
    public ORTokenElementType OR;
    public ORTokenElementType PUB;
    public ORTokenElementType PRI;
    public ORTokenElementType REC;
    public ORTokenElementType SIG;
    public ORTokenElementType STRUCT;
    public ORTokenElementType SWITCH;
    public ORTokenElementType THEN;
    public ORTokenElementType TO;
    public ORTokenElementType TRY;
    public ORTokenElementType TYPE;
    public ORTokenElementType VAL;
    public ORTokenElementType VIRTUAL;
    public ORTokenElementType WHEN;
    public ORTokenElementType WHILE;
    public ORTokenElementType WITH;
    public ORTokenElementType RAW;

    public ORTokenElementType EXCEPTION_NAME;
    public ORTokenElementType C_LOCAL_OPEN;
    public ORTokenElementType PROPERTY_NAME;
    public ORTokenElementType SHARPSHARP;
    public ORTokenElementType ARROBASE;
    public ORTokenElementType ARROW;
    public ORTokenElementType AS;
    public ORTokenElementType BACKTICK;
    public ORTokenElementType CARRET;
    public ORTokenElementType COLON;
    public ORTokenElementType COMMA;
    public ORTokenElementType COMMENT;
    public ORTokenElementType DIFF;
    public ORTokenElementType LT_OR_EQUAL;
    public ORTokenElementType GT_OR_EQUAL;
    public ORTokenElementType DOLLAR;
    public ORTokenElementType DOT;
    public ORTokenElementType DOTDOTDOT;
    public ORTokenElementType NOT_EQ;
    public ORTokenElementType NOT_EQEQ;
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
    public ORTokenElementType VARIANT_NAME;
    public ORTokenElementType PIPE;
    public ORTokenElementType PIPE_FORWARD;
    public ORTokenElementType PIPE_FIRST;
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
    public ORTokenElementType SLASHDOT;
    public ORTokenElementType SOME;
    public ORTokenElementType STAR;
    public ORTokenElementType STARDOT;
    public ORTokenElementType TAG_AUTO_CLOSE;
    public ORTokenElementType TAG_NAME;
    public ORTokenElementType TAG_LT;
    public ORTokenElementType TAG_LT_SLASH;
    public ORTokenElementType TAG_GT;
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
    public ORTokenElementType ML_STRING_OPEN;
    public ORTokenElementType ML_STRING_CLOSE;
    public ORTokenElementType JS_STRING_OPEN;
    public ORTokenElementType JS_STRING_CLOSE;
    public ORTokenElementType UNDERSCORE;
}
