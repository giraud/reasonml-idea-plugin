package com.reason.lang.ocamlyacc;

import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class OclYaccParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(OclYaccTypes.INSTANCE.COMMENT);

    private static final IFileElementType FILE = new IFileElementType(Language.findInstance(OclYaccLanguage.class));

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new OclYaccLexer();
    }

    public @NotNull TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }

    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    public @NotNull PsiParser createParser(Project project) {
        return new OclYaccParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new MlyFile(viewProvider);
    }

    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(@NotNull ASTNode node) {
        return OclYaccAstFactory.createElement(node);
    }
}
