package com.reason.lang.dune;

import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class DuneParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(DuneTypes.INSTANCE.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(DuneTypes.INSTANCE.SINGLE_COMMENT, DuneTypes.INSTANCE.MULTI_COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(DuneTypes.INSTANCE.STRING);

    private static final IFileElementType FILE = new IFileElementType(DuneLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new FlexAdapter(new DuneLexer(DuneTypes.INSTANCE));
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
        return new DuneParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new DuneFile(viewProvider);
    }

    @NotNull
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        throw new IllegalArgumentException("Not a Rescript node: " + node + " (" + type + ", " + type.getLanguage() + ")");
    }
}
