package com.reason.lang.ocaml;

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
import com.reason.ide.files.OclFile;
import com.reason.ide.files.OclInterfaceFile;
import com.reason.ide.files.OclInterfaceFileType;
import com.reason.lang.core.stub.type.ORStubElementType;
import com.reason.lang.core.stub.type.OclFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class OclParserDefinition implements ParserDefinition {
  private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
  private static final TokenSet COMMENTS = TokenSet.create(OclTypes.INSTANCE.MULTI_COMMENT);
  private static final TokenSet STRINGS = TokenSet.create(OclTypes.INSTANCE.STRING_VALUE);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new OclLexer();
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
    return new OclParser();
  }

  @NotNull
  @Override
  public IFileElementType getFileNodeType() {
    return OclFileStubElementType.INSTANCE;
  }

  @NotNull
  public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return viewProvider.getFileType() instanceof OclInterfaceFileType
        ? new OclInterfaceFile(viewProvider)
        : new OclFile(viewProvider);
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
        "Not an OCaml node: " + node + " (" + type + ", " + type.getLanguage() + "): " + node.getText());
  }
}
