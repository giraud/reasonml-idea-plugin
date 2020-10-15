package com.reason.lang.reason;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.reason.ide.files.RmlFile;
import com.reason.ide.files.RmlInterfaceFile;
import com.reason.ide.files.RmlInterfaceFileType;
import com.reason.lang.core.stub.type.ORStubElementType;
import com.reason.lang.core.stub.type.RmlFileStubElementType;
import org.jetbrains.annotations.NotNull;

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

    throw new IllegalArgumentException(
        "Not a ReasonML node: " + node + " (" + type + ", " + type.getLanguage() + ")");
  }
}
