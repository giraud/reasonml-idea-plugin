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
import com.reason.ide.files.NsFile;
import com.reason.ide.files.NsInterfaceFile;
import com.reason.ide.files.NsInterfaceFileType;
import com.reason.lang.core.stub.type.NsFileStubElementType;
import com.reason.lang.core.stub.type.ORStubElementType;
import org.jetbrains.annotations.NotNull;

public class NsParserDefinition implements ParserDefinition {
  private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
  private static final TokenSet COMMENTS =
      TokenSet.create(NsTypes.INSTANCE.MULTI_COMMENT, NsTypes.INSTANCE.SINGLE_COMMENT);
  private static final TokenSet STRINGS = TokenSet.create(NsTypes.INSTANCE.STRING_VALUE);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new NsLexer();
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return WHITE_SPACES;
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return STRINGS;
  }

  @NotNull
  public PsiParser createParser(final Project project) {
    return new NsParser();
  }

  @NotNull
  @Override
  public IFileElementType getFileNodeType() {
    return NsFileStubElementType.INSTANCE;
  }

  @NotNull
  public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return viewProvider.getFileType() instanceof NsInterfaceFileType
        ? new NsInterfaceFile(viewProvider)
        : new NsFile(viewProvider);
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
