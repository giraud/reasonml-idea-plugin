package com.reason.lang;

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
import com.reason.RmlFile;
import com.reason.lang.core.stub.type.RmlFileElementType;
import org.jetbrains.annotations.NotNull;

public class RmlParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(RmlTypes.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(RmlTypes.STRING);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new RmlLexerAdapter();
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

    @Override
    public IFileElementType getFileNodeType() {
        return RmlFileElementType.INSTANCE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new RmlFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return PsiElementFactory.createElement(node);
    }
}
