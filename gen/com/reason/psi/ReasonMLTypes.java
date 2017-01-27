// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.impl.*;

public interface ReasonMLTypes {

  IElementType CORE_TYPE = new ReasonMLElementType("CORE_TYPE");
  IElementType FUN_ARG = new ReasonMLElementType("FUN_ARG");
  IElementType FUN_BODY = new ReasonMLElementType("FUN_BODY");
  IElementType FUN_CALL = new ReasonMLElementType("FUN_CALL");
  IElementType INCLUDE_STATEMENT = new ReasonMLElementType("INCLUDE_STATEMENT");
  IElementType LET_BINDING = new ReasonMLElementType("LET_BINDING");
  IElementType LET_BINDING_BODY = new ReasonMLElementType("LET_BINDING_BODY");
  IElementType LONG_ID = new ReasonMLElementType("LONG_ID");
  IElementType MODULE_BODY = new ReasonMLElementType("MODULE_BODY");
  IElementType MODULE_EXPR = new ReasonMLElementType("MODULE_EXPR");
  IElementType MODULE_IDENT = new ReasonMLElementType("MODULE_IDENT");
  IElementType MODULE_NAME = new ReasonMLElementType("MODULE_NAME");
  IElementType MODULE_PATH = new ReasonMLElementType("MODULE_PATH");
  IElementType MODULE_STATEMENT = new ReasonMLElementType("MODULE_STATEMENT");
  IElementType RECORD_FIELD = new ReasonMLElementType("RECORD_FIELD");
  IElementType SEQ_EXPR = new ReasonMLElementType("SEQ_EXPR");
  IElementType SHORT_ID = new ReasonMLElementType("SHORT_ID");
  IElementType TUPLE_ARG = new ReasonMLElementType("TUPLE_ARG");
  IElementType TUPLE_EXPR = new ReasonMLElementType("TUPLE_EXPR");
  IElementType TUPLE_VALUE = new ReasonMLElementType("TUPLE_VALUE");
  IElementType TYPE_DEFINITION = new ReasonMLElementType("TYPE_DEFINITION");
  IElementType TYPE_STATEMENT = new ReasonMLElementType("TYPE_STATEMENT");
  IElementType UNIT = new ReasonMLElementType("UNIT");

  IElementType ARROW = new ReasonMLTokenType("ARROW");
  IElementType COLON = new ReasonMLTokenType("COLON");
  IElementType COMMA = new ReasonMLTokenType("COMMA");
  IElementType COMMENT = new ReasonMLTokenType("COMMENT");
  IElementType DOT = new ReasonMLTokenType("DOT");
  IElementType EQUAL = new ReasonMLTokenType("EQUAL");
  IElementType FLOAT = new ReasonMLTokenType("FLOAT");
  IElementType FUN = new ReasonMLTokenType("FUN");
  IElementType INCLUDE = new ReasonMLTokenType("INCLUDE");
  IElementType INT = new ReasonMLTokenType("INT");
  IElementType LBRACE = new ReasonMLTokenType("LBRACE");
  IElementType LET = new ReasonMLTokenType("LET");
  IElementType LIDENT = new ReasonMLTokenType("LIDENT");
  IElementType LPAREN = new ReasonMLTokenType("LPAREN");
  IElementType MODULE = new ReasonMLTokenType("MODULE");
  IElementType NONE = new ReasonMLTokenType("NONE");
  IElementType RBRACE = new ReasonMLTokenType("RBRACE");
  IElementType RPAREN = new ReasonMLTokenType("RPAREN");
  IElementType SEMI = new ReasonMLTokenType("SEMI");
  IElementType STRING = new ReasonMLTokenType("STRING");
  IElementType TYPE = new ReasonMLTokenType("TYPE");
  IElementType UIDENT = new ReasonMLTokenType("UIDENT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == CORE_TYPE) {
        return new ReasonMLCoreTypeImpl(node);
      }
      else if (type == FUN_ARG) {
        return new ReasonMLFunArgImpl(node);
      }
      else if (type == FUN_BODY) {
        return new ReasonMLFunBodyImpl(node);
      }
      else if (type == FUN_CALL) {
        return new ReasonMLFunCallImpl(node);
      }
      else if (type == INCLUDE_STATEMENT) {
        return new ReasonMLIncludeStatementImpl(node);
      }
      else if (type == LET_BINDING) {
        return new ReasonMLLetBindingImpl(node);
      }
      else if (type == LET_BINDING_BODY) {
        return new ReasonMLLetBindingBodyImpl(node);
      }
      else if (type == LONG_ID) {
        return new ReasonMLLongIdImpl(node);
      }
      else if (type == MODULE_BODY) {
        return new ReasonMLModuleBodyImpl(node);
      }
      else if (type == MODULE_EXPR) {
        return new ReasonMLModuleExprImpl(node);
      }
      else if (type == MODULE_IDENT) {
        return new ReasonMLModuleIdentImpl(node);
      }
      else if (type == MODULE_NAME) {
        return new ReasonMLModuleNameImpl(node);
      }
      else if (type == MODULE_PATH) {
        return new ReasonMLModulePathImpl(node);
      }
      else if (type == MODULE_STATEMENT) {
        return new ReasonMLModuleStatementImpl(node);
      }
      else if (type == RECORD_FIELD) {
        return new ReasonMLRecordFieldImpl(node);
      }
      else if (type == SEQ_EXPR) {
        return new ReasonMLSeqExprImpl(node);
      }
      else if (type == SHORT_ID) {
        return new ReasonMLShortIdImpl(node);
      }
      else if (type == TUPLE_ARG) {
        return new ReasonMLTupleArgImpl(node);
      }
      else if (type == TUPLE_EXPR) {
        return new ReasonMLTupleExprImpl(node);
      }
      else if (type == TUPLE_VALUE) {
        return new ReasonMLTupleValueImpl(node);
      }
      else if (type == TYPE_DEFINITION) {
        return new ReasonMLTypeDefinitionImpl(node);
      }
      else if (type == TYPE_STATEMENT) {
        return new ReasonMLTypeStatementImpl(node);
      }
      else if (type == UNIT) {
        return new ReasonMLUnitImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
