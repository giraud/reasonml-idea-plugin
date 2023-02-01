package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class ResParserDefinition implements ParserDefinition {
    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new ResLexer();
    }

    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(TokenType.WHITE_SPACE);
    }

    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(ResTypes.INSTANCE.MULTI_COMMENT, ResTypes.INSTANCE.SINGLE_COMMENT);
    }

    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(ResTypes.INSTANCE.STRING_VALUE);
    }

    public @NotNull PsiParser createParser(Project project) {
        return new ResParser(false);
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return ResFileStubElementType.INSTANCE;
    }

    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return viewProvider.getFileType() instanceof ResInterfaceFileType
                ? new ResInterfaceFile(viewProvider)
                : new ResFile(viewProvider);
    }

    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    public @NotNull PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        if (type instanceof ORStubElementType) {
            //noinspection rawtypes
            return ((ORStubElementType) node.getElementType()).createPsi(node);
        }

        throw new IllegalArgumentException("Not a Rescript node: " + node + " (" + type + ", " + type.getLanguage() + ")");
    }
}
