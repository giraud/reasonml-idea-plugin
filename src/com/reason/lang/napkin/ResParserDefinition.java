package com.reason.lang.napkin;

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
import com.reason.ide.files.ResFile;
import com.reason.ide.files.ResInterfaceFile;
import com.reason.ide.files.ResInterfaceFileType;
import com.reason.lang.core.stub.type.NsFileStubElementType;
import com.reason.lang.core.stub.type.ORStubElementType;
import org.jetbrains.annotations.NotNull;

public class ResParserDefinition implements ParserDefinition {

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new ResLexer();
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return TokenSet.create(TokenType.WHITE_SPACE);
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return TokenSet.create(ResTypes.INSTANCE.MULTI_COMMENT, ResTypes.INSTANCE.SINGLE_COMMENT);
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return TokenSet.create(ResTypes.INSTANCE.STRING_VALUE);
  }

  @NotNull
  public PsiParser createParser(final Project project) {
    return new ResParser();
  }

  @NotNull
  @Override
  public IFileElementType getFileNodeType() {
    return NsFileStubElementType.INSTANCE;
  }

  @NotNull
  public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return viewProvider.getFileType() instanceof ResInterfaceFileType
        ? new ResInterfaceFile(viewProvider)
        : new ResFile(viewProvider);
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
        "Not a Rescript node: " + node + " (" + type + ", " + type.getLanguage() + ")");
  }
}
