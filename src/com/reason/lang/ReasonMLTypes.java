package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.psi.ReasonMLElementType;
import com.reason.psi.ReasonMLTokenType;
import com.reason.psi.impl.*;

public interface ReasonMLTypes {

    IElementType EXTERNAL_EXPRESSION = new ReasonMLElementType("EXTERNAL_EXPRESSION");
    IElementType LET_BINDING = new ReasonMLElementType("LET_BINDING");
    IElementType LET_EXPRESSION = new ReasonMLElementType("LET_EXPRESSION");
    IElementType MODULE_NAME = new ReasonMLElementType("MODULE_NAME");
    IElementType MODULE_PATH = new ReasonMLElementType("MODULE_PATH");
    IElementType MODULE_EXPRESSION = new ReasonMLElementType("MODULE_EXPRESSION");
    IElementType OPEN_EXPRESSION = new ReasonMLElementType("OPEN_EXPRESSION");
    IElementType TYPE_CONSTR_NAME = new ReasonMLElementType("TYPE_CONSTR_NAME");
    IElementType SCOPED_EXPR = new ReasonMLElementType("SCOPED_EXPR");
    IElementType TYPE_STATEMENT = new ReasonMLElementType("TYPE_STATEMENT");
    IElementType VALUE_NAME = new ReasonMLElementType("VALUE_NAME");

    IElementType ARROBASE = new ReasonMLTokenType("ARROBASE");
    IElementType ARROW = new ReasonMLTokenType("ARROW");
    IElementType AS = new ReasonMLTokenType("AS");
    IElementType AUTO_CLOSE_TAG = new ReasonMLTokenType("AUTO_CLOSE_TAG");
    IElementType BACKTICK = new ReasonMLTokenType("BACKTICK");
    IElementType CARRET = new ReasonMLTokenType("CARRET");
    IElementType CLOSE_TAG = new ReasonMLTokenType("CLOSE_TAG");
    IElementType COLON = new ReasonMLTokenType("COLON");
    IElementType COMMA = new ReasonMLTokenType("COMMA");
    IElementType COMMENT = new ReasonMLTokenType("COMMENT");
    IElementType DOT = new ReasonMLTokenType("DOT");
    IElementType ELSE = new ReasonMLTokenType("ELSE");
    IElementType EQ = new ReasonMLTokenType("EQ");
    IElementType EQEQ = new ReasonMLTokenType("EQEQ");
    IElementType EQEQEQ = new ReasonMLTokenType("EQEQEQ");
    IElementType EXTERNAL = new ReasonMLTokenType("EXTERNAL");
    IElementType FALSE = new ReasonMLTokenType("FALSE");
    IElementType FLOAT = new ReasonMLTokenType("FLOAT");
    IElementType FUN = new ReasonMLTokenType("FUN");
    IElementType FUN_BODY = new ReasonMLTokenType("FUN_BODY");
    IElementType GT = new ReasonMLTokenType("GT");
    IElementType IF = new ReasonMLTokenType("IF");
    IElementType INCLUDE = new ReasonMLTokenType("INCLUDE");
    IElementType INT = new ReasonMLTokenType("INT");
    IElementType LBRACE = new ReasonMLTokenType("LBRACE");
    IElementType LBRACKET = new ReasonMLTokenType("LBRACKET");
    IElementType LET = new ReasonMLTokenType("LET");
    IElementType LIDENT = new ReasonMLTokenType("LIDENT");
    IElementType LIST = new ReasonMLTokenType("LIST");
    IElementType LPAREN = new ReasonMLTokenType("LPAREN");
    IElementType LT = new ReasonMLTokenType("LT");
    IElementType MINUS = new ReasonMLTokenType("MINUS");
    IElementType MINUSDOT = new ReasonMLTokenType("MINUSDOT");
    IElementType MODULE = new ReasonMLTokenType("MODULE");
    //IElementType MUL = new ReasonMLTokenType("MUL");
    //IElementType MULDOT = new ReasonMLTokenType("MULDOT");
    IElementType MUTABLE = new ReasonMLTokenType("MUTABLE");
    IElementType NONE = new ReasonMLTokenType("NONE");
    IElementType OPEN = new ReasonMLTokenType("OPEN");
    IElementType OPTION = new ReasonMLTokenType("OPTION");
    IElementType PIPE = new ReasonMLTokenType("PIPE");
    IElementType PIPE_FORWARD = new ReasonMLTokenType("PIPE_FORWARD");
    IElementType PLUS = new ReasonMLTokenType("PLUS");
    IElementType PLUSDOT = new ReasonMLTokenType("PLUSDOT");
    IElementType QUESTION_MARK = new ReasonMLTokenType("QUESTION_MARK");
    IElementType QUOTE = new ReasonMLTokenType("QUOTE");
    IElementType RBRACE = new ReasonMLTokenType("RBRACE");
    IElementType RBRACKET = new ReasonMLTokenType("RBRACKET");
    IElementType REC = new ReasonMLTokenType("REC");
    IElementType RPAREN = new ReasonMLTokenType("RPAREN");
    IElementType SEMI = new ReasonMLTokenType("SEMI");
    IElementType SHARP = new ReasonMLTokenType("SHARP");
    IElementType SHORTCUT = new ReasonMLTokenType("SHORTCUT");
    IElementType SLASH = new ReasonMLTokenType("SLASH");
    IElementType SLASHDOT = new ReasonMLTokenType("SLASHDOT");
    IElementType SOME = new ReasonMLTokenType("SOME");
    IElementType STAR = new ReasonMLTokenType("STAR");
    IElementType STARDOT = new ReasonMLTokenType("STARDOT");
    IElementType STRING = new ReasonMLTokenType("STRING");
    IElementType SWITCH = new ReasonMLTokenType("SWITCH");
    IElementType TRUE = new ReasonMLTokenType("TRUE");
    IElementType TYPE = new ReasonMLTokenType("TYPE");
    IElementType UIDENT = new ReasonMLTokenType("UIDENT");
    IElementType UNDERSCORE = new ReasonMLTokenType("UNDERSCORE");
    IElementType UNIT = new ReasonMLTokenType("UNIT");

    class Factory {
        static PsiElement createElement(ASTNode node) {
            IElementType type = node.getElementType();
            if (type == EXTERNAL_EXPRESSION) {
                return new ReasonMLExternalImpl(node);
            } else if (type == FUN_BODY) {
                return new ReasonMLFunBodyImpl(node);
            } else if (type == LET_BINDING) {
                return new ReasonMLLetBindingImpl(node);
            } else if (type == LET_EXPRESSION) {
                return new ReasonMLLetImpl(node);
            } else if (type == MODULE_NAME) {
                return new ReasonMLModuleNameImpl(node);
            } else if (type == MODULE_EXPRESSION) {
                return new ReasonMLModuleImpl(node);
            } else if (type == TYPE_CONSTR_NAME) {
                return new ReasonMLTypeConstrNameImpl(node);
            } else if (type == SCOPED_EXPR) {
                return new ReasonMLScopedExprImpl(node);
            } else if (type == TYPE_STATEMENT) {
                return new ReasonMLTypeImpl(node);
            } else if (type == VALUE_NAME) {
                return new ReasonMLValueNameImpl(node);
            }
            return new ReasonMLTokenImpl(node);
        }
    }
}
