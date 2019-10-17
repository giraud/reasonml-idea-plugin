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
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.reason.ide.files.RmlFile;
import com.reason.ide.files.RmlInterfaceFile;
import com.reason.ide.files.RmlInterfaceFileType;
import com.reason.lang.core.PsiElementFactory;
import com.reason.lang.core.stub.type.RmlFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class RmlParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(RmlTypes.INSTANCE.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(RmlTypes.INSTANCE.STRING_VALUE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new RmlLexer();
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
        return new RmlParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return RmlFileStubElementType.INSTANCE;
    }

    @NotNull
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return viewProvider.getFileType() instanceof RmlInterfaceFileType ? new RmlInterfaceFile(viewProvider) : new RmlFile(viewProvider);
    }

    @NotNull
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(@NotNull ASTNode node) {
        return PsiElementFactory.createElement(RmlTypes.INSTANCE, node);
    }
}
