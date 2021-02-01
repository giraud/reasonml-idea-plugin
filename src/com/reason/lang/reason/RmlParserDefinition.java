package com.reason.lang.reason;

import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class RmlParserDefinition implements ParserDefinition {
  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new RmlLexer();
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return TokenSet.create(TokenType.WHITE_SPACE);
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return TokenSet.create(RmlTypes.INSTANCE.MULTI_COMMENT, RmlTypes.INSTANCE.SINGLE_COMMENT);
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return TokenSet.create(RmlTypes.INSTANCE.STRING_VALUE);
  }

  @NotNull
  public PsiParser createParser(final Project project) {
    return new RmlParser();
  }

  @NotNull
  @Override
  public IFileElementType getFileNodeType() {
    return RmlFileStubElementType.INSTANCE;
  }

  @NotNull
  public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return viewProvider.getFileType() instanceof RmlInterfaceFileType
               ? new RmlInterfaceFile(viewProvider)
               : new RmlFile(viewProvider);
  }

  @NotNull
  public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }

  @NotNull
  public PsiElement createElement(@NotNull ASTNode node) {
    IElementType type = node.getElementType();
    if (type instanceof ORStubElementType) {
      //noinspection rawtypes
      return ((ORStubElementType) node.getElementType()).createPsi(node);
    }

    throw new IllegalArgumentException("Not a ReasonML node: " + node + " (" + type + ", " + type.getLanguage() + ")");
  }
}
